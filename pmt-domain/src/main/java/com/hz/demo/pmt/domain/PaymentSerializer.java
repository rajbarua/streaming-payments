package com.hz.demo.pmt.domain;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.serialization.compact.CompactReader;
import com.hazelcast.nio.serialization.compact.CompactSerializer;
import com.hazelcast.nio.serialization.compact.CompactWriter;

public class PaymentSerializer 
    implements CompactSerializer<Payment>, Serializer<Payment>{
        
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Desrialiser for Hazelcast
     */
    @Override
    public Payment read(CompactReader reader) {
        String id = reader.readString("id");
        String debtor = reader.readString("debtor");
        String creditor = reader.readString("creditor");
        Integer amount = reader.readInt32("amount");
        String currency = reader.readString("currency");
        long timestamp = reader.readInt64("timestamp");
        return new Payment(id, debtor, creditor, amount, currency, timestamp);
    }

    /**
     * Serialiser for Hazelcast
     */
    @Override
    public void write(CompactWriter writer, Payment object) {
        writer.writeString("id", object.getId());
        writer.writeString("debtor", object.getDebtor());
        writer.writeString("creditor", object.getCreditor());
        writer.writeInt32("amount", object.getAmount());
        writer.writeString("currency", object.getCurrency());
        writer.writeInt64("timestamp", object.getTimestamp());
    }

    @Override
    public String getTypeName() {
        return Payment.class.getSimpleName();
    }

    @Override
    public Class<Payment> getCompactClass() {
        return Payment.class;
    }
    
    @Override
    public byte[] serialize(String topic, Payment data) {
        //use fasterxml to serialise
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

}
