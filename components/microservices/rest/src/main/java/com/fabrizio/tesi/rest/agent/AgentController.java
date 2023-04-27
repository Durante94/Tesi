package com.fabrizio.tesi.rest.agent;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fabrizio.tesi.rest.agent.dto.AgentRequestFilter;
import com.fabrizio.tesi.rest.crud.dto.TableRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = { "/api/agent" })
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AgentController {
    @Value("${businness.manager.cachekey}")
    String agentCacheKey;

    @Autowired
    CacheManager cacheManager;

    Cache agentCache;
    ObjectMapper jsonMapper = new ObjectMapper();

    @PostConstruct
    void init() {
        agentCache = cacheManager.getCache("agents");
    }

    @GetMapping
    @ResponseBody
    public List<String> getAgents(@RequestParam(required = false, defaultValue = "{}") String filter) {
        AgentRequestFilter deserilizedFilter;
        try {
            deserilizedFilter = jsonMapper.readValue(filter, AgentRequestFilter.class);
        } catch (JsonProcessingException e) {
            log.error("DESERIALIZZAZIONE: {} in {}", filter, TableRequestDTO.class.getName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Errore formato richiesta");
        }
        return ((List<String>) agentCache.get(agentCacheKey).get())
                .stream()
                .filter(value -> deserilizedFilter.applyFiter(value))
                .skip((deserilizedFilter.getSelectedPage() - 1) * deserilizedFilter.getPageSize())
                .limit(deserilizedFilter.getPageSize())
                .collect(Collectors.toList());
    }
}