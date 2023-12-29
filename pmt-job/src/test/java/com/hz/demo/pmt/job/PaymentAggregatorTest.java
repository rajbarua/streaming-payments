package com.hz.demo.pmt.job;

import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.hazelcast.config.Config;
import com.hazelcast.config.DataConnectionConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class PaymentAggregatorTest {

    @Rule
    public KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.3"));

    @Test
    public void writeAndReadPayments() {
        //Configure and start hazelcast
        Config config = new Config();
        config.getJetConfig()
            .setResourceUploadEnabled(true)
            .setEnabled(true);

        config.addDataConnectionConfig(new DataConnectionConfig(PaymentAggregator.DATACONNECTION_NAME)
                .setType("Kafka")
                .setProperty("bootstrap.servers", kafka.getBootstrapServers())
                .setProperty("auto.offset.reset", "earliest")
                .setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
                .setProperty("value.serializer", "com.hz.demo.pmt.domain.PaymentSerializer")
                .setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringSerializer")
                .setProperty("value.deserializer", "com.hz.demo.pmt.domain.PaymentSerializer")
                .setShared(false)
        );
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);

        //Test PaymentAggregator.main() method
        PaymentAggregator.main(new String[]{kafka.getBootstrapServers()});

    }

}
