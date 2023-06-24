package com.fabrizio.tesi.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.DefaultWebSessionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest//(properties = {"keycloack.feingress=feIngrTest", "keycloack.oidcaddr=oidcTest"})
@ActiveProfiles({"default", "auth"})
// TODO: chiedere a chatgpt come importare tutti i file properties
public class ConfigControllerTest {
    @Autowired
    ConfigController controller;

    @Test
    void destroySessionTest() {
        MockServerHttpResponse response = new MockServerHttpResponse();
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        WebSession session = sessionManager.getSession(exchange).block();

        controller.destroySession(session, response);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertTrue(session.getAttributes().isEmpty());
    }
}
