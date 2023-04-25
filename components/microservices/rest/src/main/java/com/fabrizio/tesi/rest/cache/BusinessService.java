package com.fabrizio.tesi.rest.cache;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusinessService {
    WebClient client;

    @PostConstruct
    void init(@Value("${businness.manager.url}") String managerUrl) {
        client = WebClient.builder().baseUrl(managerUrl).build();
    }

    @CachePut(value = "agents", unless = "#result.size() <= 0")
    public List<String> agentsList() {
        ResponseEntity<List<String>> agents = client.get().uri(buider -> buider.path("/").build()).retrieve()
                .toEntity(new ParameterizedTypeReference<List<String>>() {
                }).block();

        if (agents.getStatusCode().equals(HttpStatus.OK))
            return agents.getBody();
        else
            return List.of();
    }
}
