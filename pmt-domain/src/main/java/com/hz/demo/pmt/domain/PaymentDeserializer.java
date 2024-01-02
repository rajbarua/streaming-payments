package com.hz.demo.pmt.domain;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.serialization.compact.CompactReader;
import com.hazelcast.nio.serialization.compact.CompactSerializer;
import com.hazelcast.nio.serialization.compact.CompactWriter;

public class PaymentDeserializer implements Deserializer<Payment>{
        
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Desrialiser for Kafka
     */
    @Override
    public Payment deserialize(String topic, byte[] data) {
        //use fasterxml to deserialise
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, Payment.class);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
