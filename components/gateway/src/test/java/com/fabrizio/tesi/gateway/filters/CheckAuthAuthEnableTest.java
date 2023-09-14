package com.fabrizio.tesi.gateway.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fabrizio.tesi.gateway.filter.CheckAuth;

@SpringBootTest(properties = {"auth.enable=true"})
public class CheckAuthAuthEnableTest {
    private static final String SESSION_ATTRIBUTE_AUTHORIZATION = "Authorization";

    @Autowired
    CheckAuth filter;
    @Mock
    GatewayFilterChain chain;

    @Value("${keycloack.feingress}")
    String host;

    @Value("${keycloack.oidcaddr}")
    String OIDCHOST;

    @Value("${keycloack.clientid}")
    String clientId;

    @Value("${keycloack.realm}")
    String realm;

    String loginUri;

    @PostConstruct
    void init() {
        loginUri = UriComponentsBuilder.fromUriString(OIDCHOST + "/realms/" + realm + "/protocol/openid-connect/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", host + "/gateway/auth")
                .queryParam("response_type", "code")
                .queryParam("scope", "openid")
//                .queryParam("nonce", nonce)
                .queryParam("response_mode", "form_post")
                .build()
                .toUri()
                .toString();
    }

    @Test
    void emptySession() {
        GatewayFilter filterChain = filter.apply(new CheckAuth.Config());
        MockServerHttpRequest request = MockServerHttpRequest.get("").build();
        MockServerWebExchange webExchnage = MockServerWebExchange.from(request);

        filterChain.filter(webExchnage, chain);

        String computedUri = webExchnage.getResponse().getHeaders().getLocation().toString(),
                comparableUri;
        int startNonce = computedUri.indexOf("nonce");
        if (startNonce >= 0) {
            comparableUri = computedUri.substring(0, startNonce);
            int endNonce = computedUri.indexOf("&", startNonce);
            if (endNonce > startNonce)
                comparableUri += computedUri.substring(endNonce + 1);
        } else
            comparableUri = computedUri;

        assertEquals(request, webExchnage.getRequest());
        assertEquals(HttpStatus.FOUND, webExchnage.getResponse().getStatusCode());
        assertEquals(loginUri, comparableUri);
    }

    @Test
    void blankSession() {
        GatewayFilter filterChain = filter.apply(new CheckAuth.Config());
        MockServerHttpRequest request = MockServerHttpRequest.get("").build();
        MockServerWebExchange webExchnage = MockServerWebExchange.from(request);

        webExchnage.getSession().block().getAttributes().put(SESSION_ATTRIBUTE_AUTHORIZATION, "");

        filterChain.filter(webExchnage, chain);

        String computedUri = webExchnage.getResponse().getHeaders().getLocation().toString(),
                comparableUri;
        int startNonce = computedUri.indexOf("nonce");
        if (startNonce >= 0) {
            comparableUri = computedUri.substring(0, startNonce);
            int endNonce = computedUri.indexOf("&", startNonce);
            if (endNonce > startNonce)
                comparableUri += computedUri.substring(endNonce + 1);
        } else
            comparableUri = computedUri;

        assertEquals(request, webExchnage.getRequest());
        assertEquals(HttpStatus.FOUND, webExchnage.getResponse().getStatusCode());
        assertEquals(loginUri, comparableUri);
    }

    @Test
    void authAttrPartialTokenSession() {
        GatewayFilter filterChain = filter.apply(new CheckAuth.Config());
        MockServerHttpRequest request = MockServerHttpRequest.get("").build();
        MockServerWebExchange webExchnage = MockServerWebExchange.from(request);

        webExchnage.getSession().block().getAttributes().put(SESSION_ATTRIBUTE_AUTHORIZATION, "test");

        try {
            filterChain.filter(webExchnage, chain);
        } catch (JWTDecodeException e) {
            assertNotNull(e);
        }
    }

    @Test
    void authAttrInvalidTokenSession() {
        GatewayFilter filterChain = filter.apply(new CheckAuth.Config());
        MockServerHttpRequest request = MockServerHttpRequest.get("").build();
        MockServerWebExchange webExchnage = MockServerWebExchange.from(request);

        webExchnage.getSession().block().getAttributes().put(SESSION_ATTRIBUTE_AUTHORIZATION, "test.test.test");

        try {
            filterChain.filter(webExchnage, chain);
        } catch (JWTDecodeException e) {
            assertNotNull(e);
        }
    }

    @Test
    void authAttrExpiredTokenSession() {
        GatewayFilter filterChain = filter.apply(new CheckAuth.Config());
        MockServerHttpRequest request = MockServerHttpRequest.get("").build();
        MockServerWebExchange webExchnage = MockServerWebExchange.from(request);

        webExchnage.getSession().block().getAttributes().put(SESSION_ATTRIBUTE_AUTHORIZATION, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZXhwIjoxNTA2MjM5MDIyLCJpYXQiOjE1MDYyMzkwMjJ9.VVD0-vIlV65JQtoHNlRuS0Gul8ptAnhzRCszIiPBBtc");

        filterChain.filter(webExchnage, chain);

        String computedUri = webExchnage.getResponse().getHeaders().getLocation().toString(),
                comparableUri;
        int startNonce = computedUri.indexOf("nonce");
        if (startNonce >= 0) {
            comparableUri = computedUri.substring(0, startNonce);
            int endNonce = computedUri.indexOf("&", startNonce);
            if (endNonce > startNonce)
                comparableUri += computedUri.substring(endNonce + 1);
        } else
            comparableUri = computedUri;

        assertEquals(request, webExchnage.getRequest());
        assertEquals(HttpStatus.FOUND, webExchnage.getResponse().getStatusCode());
        assertEquals(loginUri, comparableUri);
    }

    @Test
    void authAttrValidTokenSession() {
        GatewayFilter filterChain = filter.apply(new CheckAuth.Config());
        MockServerHttpRequest request = MockServerHttpRequest.get("").build();
        MockServerWebExchange webExchnage = MockServerWebExchange.from(request);

        webExchnage.getSession().block().getAttributes().put(SESSION_ATTRIBUTE_AUTHORIZATION, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZXhwIjoyNjk2MjM5MDIyLCJpYXQiOjE1MDYyMzkwMjJ9.KYLOU5XANljojHMULHnvm3B3QwY_CoFJoj9xMG_BLH8");

        filterChain.filter(webExchnage, chain);

        assertEquals(request, webExchnage.getRequest());
        assertNotEquals(HttpStatus.FOUND, webExchnage.getResponse().getStatusCode());
    }
}
