package com.fabrizio.tesi.rest.mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.test.context.ActiveProfiles;

import com.fabrizio.tesi.rest.agent.AgentController;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
class AgentControllerTest {
	@Value("${businness.manager.cachekey}")
	String agentCacheKey;

	@Mock
	Cache agentCache;

	@MockBean
	CacheManager manager;

	@InjectMocks
	AgentController controller;

//	@Test
	void getAllAgents() {
		List<String> defaultAgents = List.of("test");

		Mockito.when(manager.getCache(Mockito.anyString())).thenReturn(agentCache);
		Mockito.when(agentCache.get(agentCacheKey)).thenReturn(new SimpleValueWrapper(defaultAgents));

		List<ObjectNode> agents = controller.getAgents("{}");
		assertNotNull(agents);

		for (int i = 0; i < agents.size(); i++) {
			assertEquals(agents.get(i).get("value").asText(), defaultAgents.get(i));
		}
	}
}
