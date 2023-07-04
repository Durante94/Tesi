package com.fabrizio.tesi.rest.services;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
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

    @Scheduled(fixedDelayString = "${businness.manager.updatedelay}", initialDelayString = "${businness.manager.initialdelay:0}")
    @CachePut(value = "agents", key = "#root.target.agentCacheKey")
    public List<String> agentsList() {
        log.debug("Scheduled agent task");
        ResponseEntity<List<String>> agents;
        try {
            agents = client.get().uri(buider -> buider.path("/").build())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<String>>() {
                    }).block();
        } catch (WebClientResponseException e) {
            agents = ResponseEntity.status(e.getStatusCode()).build();
            log.error("Cache non aggiornata", e);
        }

        if (agents.getStatusCode().equals(HttpStatus.OK))
            return agents.getBody();
        else
            return List.of();
    }
}
