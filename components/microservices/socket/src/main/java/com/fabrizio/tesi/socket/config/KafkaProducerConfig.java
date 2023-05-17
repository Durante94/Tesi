package com.fabrizio.tesi.socket.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fabrizio.tesi.socket.dto.ConfigReqDTO;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapAddress;

    Map<String, ?> baseProduceConfigurations;

    @PostConstruct
    void init() {
        baseProduceConfigurations = new HashMap<>() {
            {
                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
                put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            }
        };
    }

    @Bean
    KafkaTemplate<String, String> defaultKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(new HashMap<>(baseProduceConfigurations) {
            {
                put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            }
        }));
    }

    @Bean
    KafkaTemplate<String, ConfigReqDTO> confRequestKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(new HashMap<>(baseProduceConfigurations) {
            {
                put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            }
        }));
    }
}
