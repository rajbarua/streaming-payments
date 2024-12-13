package com.hz.demo.pmt.job;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.kafka.KafkaSinks;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.test.TestSources;
import com.hazelcast.sql.SqlResult;
import com.hz.demo.pmt.MyConstants;
import com.hz.demo.pmt.pain001.Pain001Generator;

public class PaymentProducer implements Serializable {

    public static final String JOB_NAME = "PaymentProducer";
    public static final String SNAPSHOT_NAME = "PaymentProducer-snapshot";

    public static void main(String[] args) {
        new PaymentProducer().run();
    }

    public void run() {
        HazelcastInstance instance = Hazelcast.bootstrappedInstance();
        deployJob(instance);

    }

    private Job deployJob(HazelcastInstance instance) {
        Pipeline p = createPipeline();
        JobConfig jobConfig = new JobConfig()
                .setName(JOB_NAME)
                .addClass(PaymentProducer.class);
        SqlResult jobs = instance.getSql().execute("SHOW JOBS;");
        // drop the job if it exists
        String finalSnapshot = jobs.stream()
                .filter(row -> row.getObject("name").equals(JOB_NAME))
                .map(row -> "DROP JOB IF EXISTS \"" + JOB_NAME + "\" WITH SNAPSHOT \"" + SNAPSHOT_NAME + "\";")
                .map(sql -> instance.getSql().execute(sql))
                .findAny()
                .map(rs -> SNAPSHOT_NAME)
                .orElse(null);
        instance.getJet().getConfig().setResourceUploadEnabled(true);
        jobConfig.setInitialSnapshotName(finalSnapshot);
        return instance.getJet().newJob(p, jobConfig);
    }

    private Pipeline createPipeline() {
        Pipeline pipeline = Pipeline.create();
        pipeline
                .readFrom(TestSources.itemStream(1))// Transaction per second
                .withNativeTimestamps(10)
                .map(i -> {
                    Pain001Generator gen = new Pain001Generator();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    var doc = gen.outXML(os, 1, 50000);
                    String transaction;
                    try {
                        transaction = os.toString(StandardCharsets.UTF_8.name());
                        os.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return Tuple2.tuple2(i.timestamp()+"", transaction);
                })
                .writeTo(KafkaSinks.kafka(getKafkaProps(), MyConstants.KAFKA_TOPIC_TRANSACTIONS_CDT_TRF_TX));
        return pipeline;
    }

    private Properties getKafkaProps() {
        Properties props = new Properties();
        String bootstrapServers = "my-cluster-kafka-bootstrap.kafka:9092"; // Updated to include the namespace
        props.setProperty("bootstrap.servers", bootstrapServers);
        props.setProperty("key.deserializer", StringDeserializer.class.getCanonicalName());
        props.setProperty("value.deserializer", StringDeserializer.class.getCanonicalName());
        props.setProperty("key.serializer", StringSerializer.class.getCanonicalName());
        props.setProperty("value.serializer", StringSerializer.class.getCanonicalName());
        props.setProperty("auto.offset.reset", "earliest");

        return props;
    }

}