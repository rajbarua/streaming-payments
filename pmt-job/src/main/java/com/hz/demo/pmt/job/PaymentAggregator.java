package com.hz.demo.pmt.job;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.config.ProcessingGuarantee;
import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.DataConnectionRef;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
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
        p.readFrom(KafkaSources.kafka(dcRef, topic))
            .withNativeTimestamps(0)
            .writeTo(Sinks.logger());
        return p;
    }

}
