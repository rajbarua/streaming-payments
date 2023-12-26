CREATE OR REPLACE DATA CONNECTION kafka_dc
TYPE Kafka
NOT SHARED
OPTIONS (
    'bootstrap.servers'='my-cluster-kafka-bootstrap.kafka.svc.cluster.local:9092', 
    'key.deserializer'='org.apache.kafka.common.serialization.StringDeserializer', 
    'key.serializer'='org.apache.kafka.common.serialization.StringDeserializer',
    'value.serializer'='org.apache.kafka.common.serialization.StringDeserializer',
    'value.deserializer'='org.apache.kafka.common.serialization.StringDeserializer',
    'auto.offset.reset'='earliest');