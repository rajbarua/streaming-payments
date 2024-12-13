package com.hz.demo.pmt.job;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.Serializable;
import java.util.Properties;

import org.apache.kafka.common.serialization.StringDeserializer;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Traversers;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.core.metrics.Metrics;
import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.map.IMap;
import com.hz.demo.pmt.MyConstants;
import com.hz.demo.pmt.pain001.CdtTrfTxInf;
import com.hz.demo.pmt.pain001.Document;
import com.hz.demo.pmt.pain001.Pain001Reader;
import com.hz.demo.pmt.pain001.PmtInf;

public class ReadKafka implements Serializable{
    
    private HazelcastInstance hz;
    public static String JOB_NAME = "ReadKafka";

    public ReadKafka(HazelcastInstance hz) {
        this.hz = hz;
    }

    public static void main(String[] args) {
        HazelcastInstance hz = Hazelcast.bootstrappedInstance();
        ReadKafka rk = new ReadKafka(hz);
        var oldJob = hz.getJet().getJob(JOB_NAME);
        if (oldJob != null) {
            oldJob.cancel();
        }
        JobConfig cfg = new JobConfig();
        cfg.setName(JOB_NAME);
        hz.getJet().newJob(rk.buildPipeline(), cfg);
    }

    public Pipeline buildPipeline(){

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "my-cluster-kafka-bootstrap.kafka:9092");
        props.setProperty("key.deserializer", StringDeserializer.class.getCanonicalName());
        props.setProperty("value.deserializer", StringDeserializer.class.getCanonicalName());
        props.setProperty("auto.offset.reset", "earliest");
        Pipeline p = Pipeline.create();
        //is cooperative
        ServiceFactory<?, Pain001Reader> xmlReaderService = ServiceFactories.sharedService(ctx -> new Pain001Reader());
        StreamStage<Document> parseStage = p.readFrom(KafkaSources.kafka(props, MyConstants.KAFKA_TOPIC_TRANSACTIONS_CDT_TRF_TX))
                .withNativeTimestamps(5)
                //string to pain001 objects
                .mapUsingService(xmlReaderService, (painReader, entry) -> {
                    long start = System.currentTimeMillis();
                    Document doc = painReader.readXML(entry.getValue().toString());
                    doc.setKey(entry.getKey().toString());
                    long elapsed = System.currentTimeMillis() - start;
                    Metrics.metric("parseXML::Counter").increment();
                    Metrics.metric("parseXML::Timer").increment(elapsed);
                    return doc;
                })
                .setName("parseXML");
        /////////// XML error flow
        StreamStage<Document> errorXML = parseStage.filter(Document::isErrored).setName("ifErrorXML");
        errorXML.writeTo(Sinks.map(MyConstants.IMAP_PAIN_PARSE_ERROR, doc -> doc.getKey(), doc -> doc.getParseError()));
        // Sink<Document> activeMQSink = Sinks.<Document>jmsQueueBuilder(() -> new ActiveMQConnectionFactory("tcp://activeMQ:61616"))
        //     .destinationName("errorQ")
        //     .messageFn((session, doc) -> session.createTextMessage(doc.getParseError()))
        //     .build();
        // errorXML.writeTo(activeMQSink);
        
        /////////// XML success flow
        StreamStage<CdtTrfTxInf> cdtTrfTxInfStage = parseStage
            .filter(doc -> !doc.isErrored()).setName("ifNotErrorXML")
            //retrun individual payments
            .flatMap(document -> Traversers.traverseStream( document.getCstmrCdtTrfInitn().getPmtInf().stream()))
            //return individual transactions for each payment but enriching with payment information
            .flatMap(pmtInf -> {
                //null check
                int txCnt = pmtInf.getCdtTrfTxInf().size();
                Metrics.metric("Get CdtTrfTxInf::Counter").increment();
                //traverse pmtInf.getCdtTrfTxInf() and enrich with pmtInf
                return Traversers.traverseStream(pmtInf.getCdtTrfTxInf().stream().map(tx -> {
                    long start = System.currentTimeMillis();

                    tx.setMetadataPmtInfId(pmtInf.getPmtInfId());
                    tx.setMetadataCdtTrfTxInfCnt(txCnt);
                    tx.setMetadataDbtrAgt(pmtInf.getDbtrAgt());
                    long elapsed = System.currentTimeMillis() - start;
                    Metrics.metric("Get CdtTrfTxInf::Timer").increment(elapsed);
                    return tx;
                }));
            }).setName("Get CdtTrfTxInf");
            //validate
            StreamStage<CdtTrfTxInf> validCdtTrfTxInfStage = buildCdtTrfTxInfDedupStage(cdtTrfTxInfStage);
            //enrich
            StreamStage<CdtTrfTxInf> enrichedCdtTrfTxInfStage = buildEnrichedCdtTrfTxInfStage(validCdtTrfTxInfStage);
            //CreditorDetails
            buildCreditorDetailsStage(enrichedCdtTrfTxInfStage);
        return p;
    }
    private StreamStage<CdtTrfTxInf> buildCdtTrfTxInfDedupStage(StreamStage<CdtTrfTxInf> cdtTrfTxInfStage) {
        // Create IMaps to store in-progress and processed transaction IDs
        //TODO convert to CPMap
        ServiceFactory<?, HazelcastInstance> hazelcastInstanceService = ServiceFactories.sharedService(context -> context.hazelcastInstance());

        
        StreamStage<CdtTrfTxInf> validCdtTrfTxInfStage = cdtTrfTxInfStage
            .mapUsingService(hazelcastInstanceService, (hazelcastInstance, cdtTrfTxInf) -> {
                long start = System.currentTimeMillis();
                String e2eId = cdtTrfTxInf.getPmtId().getEndToEndId();
                IMap<String, Integer> dedupMap = hazelcastInstance.getMap(MyConstants.IMAP_PAIN_CDRTRFTXINF_DEDUP);
                Integer oldVal = dedupMap.putIfAbsent(e2eId, MyConstants.CDRTRFTXINF_DEDUP_IN_PROCESS);
                if (oldVal != null && oldVal.equals(MyConstants.CDRTRFTXINF_DEDUP_PROCESSED)) {
                    // Duplicate transaction
                    cdtTrfTxInf.setMetedataIsDuplicate(true);
                }else{
                    //oldVal != null && oldVal.equals(MyConstants.CDRTRFTXINF_DEDUP_IN_PROCESS)

                    // New transaction or tx was in process but due to failure, it was not marked as processed
                    //FIXME: A file processed multiple times at the same time will lead to duplicate processing. Add file id to key
                    cdtTrfTxInf.setMetedataIsDuplicate(false);
                }
                long elapsed = System.currentTimeMillis() - start;
                Metrics.metric("Deduplication::Counter").increment();
                Metrics.metric("Deduplication::Timer").increment(elapsed);
                return cdtTrfTxInf;
             }).setName("Deduplication");
        //sink duplicates to map
        validCdtTrfTxInfStage.filter(CdtTrfTxInf::isMetedataIsDuplicate)
            .writeTo(Sinks.map(MyConstants.IMAP_PAIN_CDRTRFTXINF_DUPLICATE, cdtTrfTxInf -> cdtTrfTxInf.getPmtId().getEndToEndId(), cdtTrfTxInf -> cdtTrfTxInf));
        validCdtTrfTxInfStage.filter(CdtTrfTxInf::isMetedataIsDuplicate)
            .writeTo(Sinks.map(MyConstants.IMAP_PAIN_CDRTRFTXINF_DEDUP, cdtTrfTxInf -> cdtTrfTxInf.getPmtId().getEndToEndId(), cdtTrfTxInf -> MyConstants.CDRTRFTXINF_DEDUP_PROCESSED));
        //filter non duplicates to proceed
        return validCdtTrfTxInfStage.filter(cdtTrfTxInf -> !cdtTrfTxInf.isMetedataIsDuplicate());
    }

    /**
     * Enriches CdtTrfTxInf with metadata from BICCache, CifAccountMap and others
     */
    private StreamStage<CdtTrfTxInf> buildEnrichedCdtTrfTxInfStage(StreamStage<CdtTrfTxInf> cdtTrfTxInfStage) {
        StreamStage<CdtTrfTxInf> enrichedCdtTrfTxInfStage = cdtTrfTxInfStage
        .mapUsingIMap("BICCache", cdtTrfTxInf -> cdtTrfTxInf.getCdtrAgt().getFinInstnId().getBic(), 
                (cdtTrfTxInf, bicAlias)-> {
                    cdtTrfTxInf.getCdtrAgt().getFinInstnId().setMetadataBicAlias(bicAlias);
                    return cdtTrfTxInf;
                })
        .mapUsingIMap("CifAccountMap", cdtTrfTxInf -> cdtTrfTxInf.getCdtrAgt().getFinInstnId().getMetadataBicAlias(), 
                (cdtTrfTxInf, accNum)-> {
                    cdtTrfTxInf.getCdtrAgt().getFinInstnId().setMetadataAccNum(accNum);
                    return cdtTrfTxInf;
                });
        return enrichedCdtTrfTxInfStage;
    }

    /**
     * For each payment, find all unique creditors then 
     * @param cdtTrfTxInfStage
     */
    private void buildCreditorDetailsStage(StreamStage<CdtTrfTxInf> cdtTrfTxInfStage) {
        //group by payment information id. In other words, for each payment
        StreamStage<PmtInf> mapStateful = cdtTrfTxInfStage.groupingKey(cdtTrfTxInf -> cdtTrfTxInf.getMetadataPmtInfId())
        //lets start processing the transactions per payment
        .mapStateful(SECONDS.toMillis(5), 
            PmtInf::new, //new PmtInf object to hold state. Needs repopulation
            (pmtInf, pmtInfId, entry)->{
                long start = System.currentTimeMillis();
                //populate PmtInf object from cdtTrfTxInf
                pmtInf.setMetadataCounter(pmtInf.getMetadataCounter()+1);
                pmtInf.setPmtInfId(entry.getMetadataPmtInfId());
                pmtInf.setDbtrAgt(entry.getMetadataDbtrAgt());
                pmtInf.addMetadataCreditorDetail(entry);
                pmtInf.addCdtTrfTxInf(entry);//required to update completion status
                if(pmtInf.getMetadataCounter().equals(entry.getMetadataCdtTrfTxInfCnt()) ){
                    //all transactions for this payment are processed
                    long elapsed = System.currentTimeMillis() - start;
                    Metrics.metric("CreditorDetails Go::Counter").increment();
                    Metrics.metric("CreditorDetails Go::Timer").increment(elapsed);
                    return pmtInf;
                } else {
                    //not all transactions are processed
                    long elapsed = System.currentTimeMillis() - start;
                    Metrics.metric("CreditorDetails Wait::Counter").increment();
                    Metrics.metric("CreditorDetails Wait::Timer").increment(elapsed);
                    return null;
                }
            },
            (pmtInf, pmtInfId, time)-> pmtInf).setName("CreditorDetails");
        //write Creditory details
        mapStateful.writeTo(Sinks.map(MyConstants.IMAP_PAIN_CRD_DTL, pmtInf -> pmtInf.getPmtInfId(), pmtInf -> pmtInf));
        //also mark completion of processing for the tx
        mapStateful.flatMap(pmtInf -> Traversers.traverseIterable(pmtInf.getCdtTrfTxInf()))
            .writeTo(Sinks.map(MyConstants.IMAP_PAIN_CDRTRFTXINF_DEDUP, cdtTrfTxInf -> cdtTrfTxInf.getPmtId().getEndToEndId(), cdtTrfTxInf -> MyConstants.CDRTRFTXINF_DEDUP_PROCESSED));
    }

    

}
