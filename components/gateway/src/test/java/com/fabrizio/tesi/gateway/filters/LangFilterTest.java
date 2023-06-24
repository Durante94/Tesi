package com.fabrizio.tesi.gateway.filters;

import com.fabrizio.tesi.gateway.filter.LangFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class LangFilterTest {
    @Autowired
    LangFilter filter;
    @Mock
    GatewayFilterChain chain;

    @Test
    void langFilterNoAttrTest() {
        GatewayFilter filterChain = filter.apply(new LangFilter.Config());
        MockServerHttpRequest request = MockServerHttpRequest.get("").build();
        MockServerWebExchange webExchnage = MockServerWebExchange.from(request);

        filterChain.filter(webExchnage, chain);

        String langAttr = webExchnage.getSession().share().block().getAttribute("lang");

        assertEquals(request, webExchnage.getRequest());
        assertNull(langAttr);
    }

    @Test
    void langFilterAttrTest() {
        String testSessionAttribute = "test";
        GatewayFilter filterChain = filter.apply(new LangFilter.Config());
        MockServerHttpRequest request = MockServerHttpRequest.get("").build();
        MockServerWebExchange webExchnage = MockServerWebExchange.from(request);

        webExchnage.getSession().block().getAttributes().put("lang", testSessionAttribute);

        filterChain.filter(webExchnage, chain);

        String langAttr = webExchnage.getSession().share().block().getAttribute("lang");

        assertEquals(request, webExchnage.getRequest());
        assertEquals(testSessionAttribute, langAttr);
    }
}
