package com.fabrizio.tesi.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

@Component
public class LangFilter extends AbstractGatewayFilterFactory<LangFilter.Config> {
	public LangFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {

		return (exchange, chain) -> {
			WebSession session = exchange.getSession().share().block();
			if (session.getAttribute("lang") != null) {
				ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
						.header("lang", session.getAttribute("lang").toString()).build();
				return chain.filter(exchange.mutate().request(modifiedRequest).build());
			}
			return chain.filter(exchange);

		};
	}

	public static class Config {

	}
}
