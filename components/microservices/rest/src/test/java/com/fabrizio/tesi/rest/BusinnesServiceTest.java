package com.fabrizio.tesi.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient;

import com.fabrizio.tesi.rest.services.BusinessService;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("dev")
class BusinnesServiceTest {
	@Mock
	WebClient webClient;

	@Autowired
	BusinessService service;

	@SuppressWarnings("unchecked")
//	@Test
	void responseTest() {
		RequestHeadersUriSpec<?> mockRequestHeaders = Mockito.mock(RequestHeadersUriSpec.class);

		Mockito.when(WebClient.builder().baseUrl(Mockito.anyString()).build()).thenReturn(webClient);
//		Mockito.when(webClient.get()).thenReturn(mockRequestHeaders);

		List<String> agents = service.agentsList();

		assertTrue(agents.isEmpty());
	}

}
