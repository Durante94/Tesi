package com.fabrizio.tesi.gateway.config;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.DefaultWebSessionManager;

import com.fabrizio.tesi.gateway.dto.CallbackRequest;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;

@SpringBootTest(properties = { "keycloack.oidcaddr=http://localhost:50000/auth" })
@FieldDefaults(level = AccessLevel.PRIVATE)
class ConfigControllerMockServerTest {
	static MockWebServer mockBackEnd;

	@Autowired
	ConfigController controller;

	@Value("${keycloack.feingress}")
	String entryPoint;

	MockServerHttpResponse response;
	WebSession session;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		mockBackEnd = new MockWebServer();
		mockBackEnd.start(50000);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		mockBackEnd.shutdown();
	}

	@BeforeEach
	void init() {
		response = new MockServerHttpResponse();
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		session = sessionManager.getSession(exchange).block();
	}

	@Test
	void destroySessionWithInvalidTokenTest() {
		session.getAttributes().put("RefreshAuth", "cacellami.cacellami.cacellami");

		mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.UNAUTHORIZED.value()));

		controller.destroySession(session, response);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(session.getAttributes().isEmpty());
	}

	@Test
	void destroySessionWithPseudoValidTokenTest() {
		session.getAttributes().put("RefreshAuth", "cacellami.cacellami.cacellami");

		mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.NO_CONTENT.value()));

		controller.destroySession(session, response);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(session.getAttributes().isEmpty());
	}

	@Test
	void getAuthNullRequestCallbackTest() {
		controller.getAuth(session, null, response);

		assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
		assertEquals(URI.create(entryPoint + "/index.html"), response.getHeaders().getLocation());
	}

	@Test
	void getAuthRequestCallbackNoCodeTest() {
		CallbackRequest dto = new CallbackRequest();
		mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

		try {
			controller.getAuth(session, dto, response);
		} catch (RuntimeException e) {
			assertNotNull(e);
		}
	}

	@Test
	void getAuthRequestCallbackEmptyBodyTest() {
		CallbackRequest dto = new CallbackRequest();
		Buffer body = new Buffer();
		mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody(body));

		controller.getAuth(session, dto, response);

		assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
		assertEquals(URI.create(entryPoint + "/index.html"), response.getHeaders().getLocation());
	}

	@Test
	void getAuthRequestCallbackBlankResponseTest() {
		CallbackRequest dto = new CallbackRequest();
		String body = "{}";
		mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody(body));

		controller.getAuth(session, dto, response);

		assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
		assertEquals(URI.create(entryPoint + "/index.html"), response.getHeaders().getLocation());
	}

	@Test
	void getAuthRequestCallbackBlankAccesTokenTest() {
		CallbackRequest dto = new CallbackRequest();
		String body = "{\"access_token\":\"\"}";
		mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody(body));

		controller.getAuth(session, dto, response);

		assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
		assertEquals(URI.create(entryPoint + "/index.html"), response.getHeaders().getLocation());
	}
	
	@Test
	void getAuthRequestCallbackBlankRefreshTokenTest() {
		CallbackRequest dto = new CallbackRequest();
		String body = "{"
				+ "\"access_token\":\"\","
				+ "\"refresh_token\":\"\""
				+ "}";
		mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody(body));

		controller.getAuth(session, dto, response);

		assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
		assertEquals(URI.create(entryPoint + "/index.html"), response.getHeaders().getLocation());
	}

}
