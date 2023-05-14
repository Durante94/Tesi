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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
    ObjectMapper jsonMapper;

    @PostConstruct
    void init() {
        agentCache = cacheManager.getCache("agents");
        jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @GetMapping
    @ResponseBody
    public List<ObjectNode> getAgents(@RequestParam(required = false, defaultValue = "{}") String filter) {
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
                .skip(deserilizedFilter.getSelectedPage() * deserilizedFilter.getPageSize())
                .limit(deserilizedFilter.getPageSize())
                .map(agent -> jsonMapper.createObjectNode().put("value", agent))
                .collect(Collectors.toList());
    }
}
