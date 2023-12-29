package com.hz.demo.pmt.kafka;

import io.micrometer.common.KeyValues;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.micrometer.KafkaRecordSenderContext;
import org.springframework.kafka.support.micrometer.KafkaTemplateObservationConvention;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hz.demo.pmt.domain.Payment;

@SpringBootApplication
@EnableScheduling
public class PaymentProducerSpring {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentProducerSpring.class);

    public static void main(String[] args) {
        SpringApplication.run(PaymentProducerSpring.class, args);
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name("payments")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public KafkaTemplate<String, Payment> kafkaTemplate(ProducerFactory<String, Payment> producerFactory) {
        KafkaTemplate<String, Payment> t = new KafkaTemplate<>(producerFactory);
        t.setObservationEnabled(true);
        t.setObservationConvention(new KafkaTemplateObservationConvention() {
            @Override
            public KeyValues getLowCardinalityKeyValues(KafkaRecordSenderContext context) {
                return KeyValues.of("topic", context.getDestination(),
                        "id", String.valueOf(context.getRecord().key()));
            }
        });
        return t;
    }

}
