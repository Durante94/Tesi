package com.fabrizio.tesi.socket.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.fabrizio.tesi.socket.dto.AlarmPayload;
import com.fabrizio.tesi.socket.dto.ConfigRespPayload;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@EnableKafka
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapAddress;
    @Value("${spring.kafka.consumer.group-id}")
    String groupId;

    Map<String, Object> props;

    @PostConstruct
    void initPorps() {
        props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    }

    @Bean
    ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(new HashMap<>(props) {
            {
                put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            }
        });
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    ConsumerFactory<String, ConfigRespPayload> consumerConfigFactory() {
        return new DefaultKafkaConsumerFactory<>(new HashMap<>(props) {
            {
                put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            }
        }, new StringDeserializer(), new ErrorHandlingDeserializer<>(new JsonDeserializer<>(ConfigRespPayload.class)));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ConfigRespPayload> kafkaListenerContainerConfigFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConfigRespPayload> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerConfigFactory());
        return factory;
    }

    @Bean
    ConsumerFactory<String, AlarmPayload> consumerAlarmFactory() {
        return new DefaultKafkaConsumerFactory<>(new HashMap<>(props) {
            {
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            }
        }, new StringDeserializer(), new ErrorHandlingDeserializer<>(new JsonDeserializer<>(AlarmPayload.class)));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, AlarmPayload> kafkaListenerContainerAlarmFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AlarmPayload> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerAlarmFactory());
        return factory;
    }
}
