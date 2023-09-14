package com.fabrizio.tesi.gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

@SpringBootTest(properties = {"keycloack.oidcaddr=http://localhost:50000/auth", "auth.secret="})
@FieldDefaults(level = AccessLevel.PRIVATE)
class ConfigControllerMockServerInvalidSecretTest {
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
    void getAuthRequestCallbackBlankRefreshTokenTest() {
        CallbackRequest dto = new CallbackRequest();
        String[] bodies = new String[]{
                "{"
                        + "\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyZXNvdXJjZV9hY2Nlc3MiOnsidGVzaS1jbGllbnQiOiJ7XCJyb2xlc1wiOltcImFuYWxpc3RcIl19In19.f4N_LLTX2ENUO6cp435P15Lmt9izqQa671eXmKXsTfI\","
                        + "\"refresh_token\":\"\","
                        + "\"id_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1MTYyMzkwMjIsImV4cCI6MTUxNjIzOTAyMn0.BA8MjTQQmXNtRSOhTvnibDjbtx7M5KO8M4XLQu-Oizg\","
                        + "\"expires_in\": 1505468754"
                        + "}"
        };

        for (String body : bodies) {
            mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody(body));

            controller.getAuth(session, dto, response);
            assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
            assertEquals(URI.create(entryPoint + "/web/"), response.getHeaders().getLocation());
        }
    }

}
