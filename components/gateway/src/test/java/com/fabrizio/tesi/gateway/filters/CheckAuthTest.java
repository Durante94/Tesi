package com.fabrizio.tesi.gateway.filters;

import com.fabrizio.tesi.gateway.filter.CheckAuth;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CheckAuthTest {
    @Autowired
    CheckAuth filter;
    @Mock
    GatewayFilterChain chain;

    @Test
    void emptySessionAuthDisabled() {
        GatewayFilter filterChain = filter.apply(new CheckAuth.Config());
        MockServerHttpRequest request = MockServerHttpRequest.get("").build();
        MockServerWebExchange webExchnage = MockServerWebExchange.from(request);

        filterChain.filter(webExchnage, chain);

        assertEquals(request, webExchnage.getRequest());
    }
}
