package com.fabrizio.tesi.rest.services;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
// @Profile("prod")
public class BusinessService {
    @Value("${businness.manager.cachekey}")
    public String agentCacheKey;

    @Value("${businness.manager.url}")
    String managerUrl;

    WebClient client;

    @PostConstruct
    void init() {
        client = WebClient.builder().baseUrl(managerUrl).build();
    }

    @Scheduled(fixedDelayString = "${businness.manager.updatedelay}")
    @CachePut(value = "agents", key = "#root.target.agentCacheKey", unless = "#result.size() <= 0")
    public List<String> agentsList() {
        ResponseEntity<List<String>> agents = client.get().uri(buider -> buider.path("/").build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<String>>() {
                }).block();

        log.debug("Scheduled agent task");
        if (agents.getStatusCode().equals(HttpStatus.OK))
            return agents.getBody();
        else
            return List.of();
    }
}
