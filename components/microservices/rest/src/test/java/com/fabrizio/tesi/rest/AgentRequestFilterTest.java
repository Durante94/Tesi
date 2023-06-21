package com.fabrizio.tesi.rest;

import com.fabrizio.tesi.rest.agent.dto.AgentRequestFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("dev")
public class AgentRequestFilterTest {

    @Test
    void filterTest() {
        List<String> cacheResult = List.of("test", "prova");
        AgentRequestFilter filter = new AgentRequestFilter();
        filter.setFilter("es");

        List<String> result = cacheResult.stream().filter(filter::applyFiter).collect(Collectors.toList());
        assertFalse(result.isEmpty());
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), cacheResult.get(0));
    }
}
