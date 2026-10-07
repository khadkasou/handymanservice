package com.nepbrain.technology.core.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Producer Configuration
 * - Optimized for reliability, batching, and idempotent message delivery.
 * - Only active when spring.kafka.producer.bootstrap-servers is set.
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.kafka.producer", name = "bootstrap-servers")
public class KafkaConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.acks:all}")
    private String acksConfig;

    @Value("${spring.kafka.producer.batch-size:16384}")
    private int batchSize;

    @Value("${spring.kafka.producer.linger-ms:10}")
    private int lingerMs;

    @Value("${spring.kafka.producer.buffer-memory:33554432}")
    private long bufferMemory;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        // Basic connection
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Reliability and idempotence
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, acksConfig);   // "all" ensures full commit before ack
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // Required for idempotence

        // Performance
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);

        // Payload limit (20 MB)
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 20 * 1024 * 1024);

        JacksonJsonSerializer<Object> valueSerializer = new JacksonJsonSerializer<>().noTypeInfo();
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), valueSerializer);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
