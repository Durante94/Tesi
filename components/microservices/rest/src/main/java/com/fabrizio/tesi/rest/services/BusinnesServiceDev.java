package com.fabrizio.tesi.rest.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
// @Profile("dev")
public class BusinnesServiceDev {
    @Value("${businness.manager.cachekey}")
    public String agentCacheKey;

    // @Scheduled(fixedDelayString = "${businness.manager.updatedelay}")
    // @CachePut(value = "agents", key = "#root.target.agentCacheKey", unless = "#result.size() <= 0")
    // public List<String> agentsList() {
    //     log.debug("Scheduled agent task");
    //     return List.of("test", "test1");
    // }
}

