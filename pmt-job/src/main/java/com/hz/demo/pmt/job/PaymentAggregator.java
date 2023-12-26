package com.hz.demo.pmt.job;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.DataConnectionRef;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;

/**
 * A Hazelcast Jet job that reads incoming payments from a Kafka topic and
 * aggregates them by payment bank to total their liquidity position.
 */
public class PaymentAggregator {

    // the topic read from environment variable
    private static final String TOPIC = "payments";
    private static final String JOB_NAME = "payment-aggregator";

    public static void main(String[] args) {
        new PaymentAggregator().run(TOPIC);
    }

    private void run(String topic) {
        HazelcastInstance instance = Hazelcast.bootstrappedInstance();
        Pipeline p = createPipeline(topic);
        JobConfig jobConfig = new JobConfig()
            .setName(JOB_NAME)
            .addClass(PaymentAggregator.class);
        Job oldJob = instance.getJet().getJob(JOB_NAME);
        if (oldJob != null) {
            oldJob.cancel();
        }
        instance.getJet().newJob(p, jobConfig);


    }

    private Pipeline createPipeline(String topic) {
        DataConnectionRef dcRef = DataConnectionRef.dataConnectionRef("kafka_dc");
        Pipeline p = Pipeline.create();
        p.readFrom(KafkaSources.kafka(dcRef, topic))
            .withNativeTimestamps(0)
            .writeTo(Sinks.logger());
        return p;
    }

}
