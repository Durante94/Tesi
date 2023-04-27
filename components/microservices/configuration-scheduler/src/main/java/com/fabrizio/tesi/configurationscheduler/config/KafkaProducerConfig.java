package com.fabrizio.tesi.configurationscheduler.config;

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

import com.fabrizio.tesi.configurationscheduler.dto.ConfigRequest;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaProducerConfig {
    @Value("${kafka.bootstrap-servers}")
    String kafkaAddress;

    Map<String, ?> baseProduceConfigurations;

    @PostConstruct
    void init() {
        baseProduceConfigurations = new HashMap<>() {
            {
                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
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
    KafkaTemplate<String, ConfigRequest> confRequestKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(new HashMap<>(baseProduceConfigurations) {
            {
                put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            }
        }));
    }
}
