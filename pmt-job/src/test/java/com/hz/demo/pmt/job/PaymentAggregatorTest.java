package com.hz.demo.pmt.job;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.DockerImageName;

import com.hazelcast.config.Config;
import com.hazelcast.config.DataConnectionConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Job;
import com.hz.demo.pmt.domain.Payment;
import com.hz.demo.pmt.domain.PaymentSerializer;

// @Testcontainers
public class PaymentAggregatorTest {

    // @Rule
    // @Container
    public KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.3"));

    // @Test
    public void writeAndReadPayments() throws InterruptedException, ExecutionException {
        String topicName = "payments";
        String bootstrapServers = kafka.getBootstrapServers();
        KafkaProducer<String, Payment> producer = 
            new KafkaProducer<>(ImmutableMap.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers, 
                                                ConsumerConfig.GROUP_ID_CONFIG, "collector-test", 
                                                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"),
                                    new StringSerializer(), new PaymentSerializer());
        producer.send(new org.apache.kafka.clients.producer.ProducerRecord<>(topicName, "uid1", new Payment("uid1", "debtor1", "creditor1", 100, "EUR", 0)));
        //Configure and start hazelcast
        Config config = new Config();
        config.getJetConfig()
            .setResourceUploadEnabled(true)
            .setEnabled(true);

        config.addDataConnectionConfig(new DataConnectionConfig(PaymentAggregator.DATACONNECTION_NAME)
                .setType("Kafka")
                .setProperty("bootstrap.servers", bootstrapServers)
                .setProperty("auto.offset.reset", "earliest")
                .setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
                .setProperty("value.serializer", "com.hz.demo.pmt.domain.PaymentSerializer")
                .setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
                .setProperty("value.deserializer", "com.hz.demo.pmt.domain.PaymentDeserializer")
                .setShared(false)
        );
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
        new PaymentAggregator().deployJob(hz, topicName);
        assertNotNull(hz.getJet().getJob(PaymentAggregator.JOB_NAME));
        Job job = hz.getJet().getJob(PaymentAggregator.JOB_NAME);
        Thread.sleep(1000);
        System.out.println("Status is "+job.getStatus());
        


    }

}
