package com.fabrizio.tesi.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import com.fabrizio.tesi.rest.agent.AgentController;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("dev")
class AgentControllerTest {
    @Autowired
    AgentController controller;

    @Test
    void getAllAgents() {
        List<ObjectNode> agents = controller.getAgents("{}");
        assertNotNull(agents);
    }

    @Test
    void invalidJsonFilterTest() {
        try {
            controller.getAgents("");
        } catch (ResponseStatusException e) {
            assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    void getFilteredAgents() {
        List<ObjectNode> agents = controller.getAgents("{\"filter\": \"a\"}");
        assertNotNull(agents);
    }
}
