package com.fabrizio.tesi.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenRelayFilter extends AbstractGatewayFilterFactory<TokenRelayFilter.Config> {

    public TokenRelayFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            WebSession session = exchange.getSession().share().block();
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().headers(httpHeaders -> {
                httpHeaders.set("Authorization", session.getAttribute("Authorization").toString());
            }).build();
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    public static class Config {

    }
}
