package com.fabrizio.tesi.gateway.filters;

import com.fabrizio.tesi.gateway.filter.TokenRelayFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TokenRelayFilterTest {
    @Autowired
    TokenRelayFilter filter;
    @Mock
    GatewayFilterChain chain;

    @Test
    void langFilterAttrTest() {
        String testSessionAttribute = "test";
        GatewayFilter filterChain = filter.apply(new TokenRelayFilter.Config());
        MockServerHttpRequest request = MockServerHttpRequest.get("").build();
        MockServerWebExchange webExchnage = MockServerWebExchange.from(request);

        webExchnage.getSession().block().getAttributes().put("Authorization", testSessionAttribute);

        filterChain.filter(webExchnage, chain);

        // String langAttr = webExchnage.getSession().share().block().getAttribute("Authorization");
        String langAttr = webExchnage.getRequiredAttribute("Authorization");

        assertNotEquals(request, webExchnage.getRequest());
        assertEquals(testSessionAttribute, langAttr);
    }
}
