package com.hz.demo.pmt.job;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.config.ProcessingGuarantee;
import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.DataConnectionRef;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hz.demo.pmt.domain.Payment;
import com.hz.demo.pmt.domain.PaymentSerializer;

/**
 * A Hazelcast Jet job that reads incoming payments from a Kafka topic and
 * aggregates them by payment bank to total their liquidity position.
 */
public class PaymentAggregator {

    // the topic read from environment variable
    public static final String TOPIC = "payments";
    public static final String JOB_NAME = "payment-aggregator";
    public static final String DATACONNECTION_NAME = "kafka_dc";

    public static void main(String[] args) {
        new PaymentAggregator().run(TOPIC);
    }

    private void run(String topic) {
        HazelcastInstance instance = Hazelcast.bootstrappedInstance();
        deployJob(instance, topic);
    }

    public void deployJob(HazelcastInstance instance, String topic) {
        Pipeline p = createPipeline(topic);
        JobConfig jobConfig = new JobConfig()
            .setName(JOB_NAME)
            .setProcessingGuarantee(ProcessingGuarantee.EXACTLY_ONCE)
            .addClass(PaymentAggregator.class)
            .addClass(Payment.class)
            .addClass(PaymentSerializer.class);
        Job oldJob = instance.getJet().getJob(JOB_NAME);
        if (oldJob != null) {
            oldJob.cancel();
        }
        instance.getJet().getConfig().setResourceUploadEnabled(true);
        instance.getJet().newJob(p, jobConfig);
    }
    private Pipeline createPipeline(String topic) {
        DataConnectionRef dcRef = DataConnectionRef.dataConnectionRef(DATACONNECTION_NAME);
        Pipeline p = Pipeline.create();
        StreamStage<Payment> sourceStage 
            = p.readFrom(KafkaSources.<String, Payment, Payment>kafka(dcRef, ConsumerRecord::value, topic))
            .withNativeTimestamps(0);
            // .peek(val -> true, val -> "Received value of type " + val.getValue().getClass().getName())
        //branch out to 2 sinks
        sourceStage.writeTo(Sinks.logger((pmt)->"Creditor Sink " + pmt.getCreditor()));
        sourceStage.writeTo(Sinks.logger((pmt)->"Debtor Sink " + pmt.getDebtor()));
        return p;
    }

}
