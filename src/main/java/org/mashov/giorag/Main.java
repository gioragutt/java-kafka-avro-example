package org.mashov.giorag;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.*;
import org.mashov.giorag.avro.LogMessage;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        Producer<String, Object> producer = new KafkaProducer<>(props);

        while (true) {
            LogMessage logMessage = LogMessage.newBuilder()
                    .setContent("Hello From Java!!!")
                    .setLevel("DEBUG")
                    .setSource("main")
                    .setTimestamp(Instant.now())
                    .build();

            ProducerRecord<String, Object> record =
                    new ProducerRecord<>("log_messages", "key", logMessage);
            RecordMetadata recordMetadata = producer.send(record).get();
            System.out.println(record);
            System.out.println(recordMetadata);
            Thread.sleep(1000);
        }
    }
}
