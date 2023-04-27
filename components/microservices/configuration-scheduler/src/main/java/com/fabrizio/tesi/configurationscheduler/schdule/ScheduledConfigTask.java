package com.fabrizio.tesi.configurationscheduler.schdule;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fabrizio.tesi.configurationscheduler.common.GenericAdapter;
import com.fabrizio.tesi.configurationscheduler.dto.CRUDDTO;
import com.fabrizio.tesi.configurationscheduler.dto.ConfigRequest;
import com.fabrizio.tesi.configurationscheduler.entity.CRUDEntity;
import com.fabrizio.tesi.configurationscheduler.repository.CRUDRepository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduledConfigTask {
    @Value("${kafka.consumer-topic}")
    String confRequestTopic;

    @Autowired
    CacheManager manager;

    @Autowired
    CRUDRepository crudRepository;

    @Autowired
    KafkaTemplate<String, ConfigRequest> templateMsg;

    Cache cache;

    @PostConstruct
    void init() {
        cache = manager.getCache("entities");
    }

    @Scheduled(fixedDelayString = "${businness.manager.updatedelay}")
    void entitiesCheck() {
        GenericAdapter<CRUDEntity, CRUDDTO> adapter = new GenericAdapter<>(CRUDEntity.class, CRUDDTO.class);
        crudRepository.findAll().stream().forEach(entity -> {
            AtomicBoolean sendMessagge = new AtomicBoolean(false);
            Optional<CRUDDTO> fromCache = Optional.ofNullable(cache.get(entity.getId(), CRUDDTO.class));
            CRUDDTO fromDB = adapter.enityToDto(entity);

            fromCache.ifPresentOrElse(
                    dto -> sendMessagge.set(!dto.equals(fromDB) | !(dto.hashCode() == fromDB.hashCode())),
                    () -> sendMessagge.set(true));

            if (sendMessagge.get()) {
                cache.put(entity.getId(), fromDB);
                templateMsg.send(confRequestTopic, "toggle",
                        new ConfigRequest().enable(entity.isEnable()).agent(entity.getAgentId()));
            }
        });
    }
}
