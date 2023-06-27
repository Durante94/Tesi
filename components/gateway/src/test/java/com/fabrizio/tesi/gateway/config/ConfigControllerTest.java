package com.fabrizio.tesi.gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.DefaultWebSessionManager;

@SpringBootTest(properties = { "auth.enable=true" })
public class ConfigControllerTest {
	@Autowired
	ConfigController controller;

	@Value("${keycloack.feingress}")
	String entryPoint;

	@Test
	void destroySessionTest() {
		MockServerHttpResponse response = new MockServerHttpResponse();
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		WebSession session = sessionManager.getSession(exchange).block();
		session.getAttributes().put("test", "to be canceled");

		controller.destroySession(session, response);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(session.getAttributes().isEmpty());
	}

	@Test
	void destroySessionWithBlankTokenTest() {
		MockServerHttpResponse response = new MockServerHttpResponse();
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		WebSession session = sessionManager.getSession(exchange).block();
		session.getAttributes().put("RefreshAuth", "");

		controller.destroySession(session, response);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(session.getAttributes().isEmpty());
	}

	@Test
	void destroySessionWithBrokenTokenTest() {
		MockServerHttpResponse response = new MockServerHttpResponse();
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		WebSession session = sessionManager.getSession(exchange).block();
		session.getAttributes().put("RefreshAuth", "cacellami");

		controller.destroySession(session, response);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(session.getAttributes().isEmpty());
	}

	@Test
	void destroySessionWithInvalidTokenNoOauthResponseTest() {
		MockServerHttpResponse response = new MockServerHttpResponse();
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		WebSession session = sessionManager.getSession(exchange).block();
		session.getAttributes().put("RefreshAuth", "cacellami.cacellami.cacellami");

		controller.destroySession(session, response);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(session.getAttributes().isEmpty());
	}

	@Test
	void authenticatedTest() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		WebSession session = sessionManager.getSession(exchange).block();

		ResponseEntity<Boolean> response = controller.authenticated(session);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertFalse(response.getBody());
	}

	@Test
	void reDoLoginTest() {
		MockServerHttpResponse response = new MockServerHttpResponse();
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		WebSession session = sessionManager.getSession(exchange).block();

		controller.reDoLogin(session, response);

		assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
		assertNotNull(response.getHeaders());
		assertNotNull(response.getHeaders().getLocation());
		assertEquals(URI.create(entryPoint + "/web/"), response.getHeaders().getLocation());
	}
}
