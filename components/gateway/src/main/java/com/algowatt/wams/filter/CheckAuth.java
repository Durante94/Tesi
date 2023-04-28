package com.algowatt.wams.filter;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;
import org.springframework.web.util.UriComponentsBuilder;

import com.algowatt.wams.config.JwtService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckAuth extends AbstractGatewayFilterFactory<CheckAuth.Config> {
	
	@Autowired
	JwtService jwtService;
	
	@Value("${keycloack.feingress}")
	String host;
	
	@Value("${keycloack.oidcaddr}")
	String OIDCHOST;
	
	@Value("${keycloack.clientid}")
	String clientId;
	
	@Value("${keycloack.realm}")
	String realm;
	
	public CheckAuth() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
	    return (exchange, chain) -> {
	        WebSession session = exchange.getSession().share().block();
	        if (jwtService.checkTokenState(session)) {
				return chain.filter(exchange);
	        } else {
	            String nonce = UUID.randomUUID().toString();
	            URI loginUri = UriComponentsBuilder.fromUriString(OIDCHOST + "/realms/" + realm + "/protocol/openid-connect/auth")
	                    .queryParam("client_id", clientId)
						.queryParam("redirect_uri", host + "/gateway/auth")
	                    .queryParam("response_type", "code")
	                    .queryParam("scope", "openid")
	                    .queryParam("nonce", nonce)
	                    .queryParam("response_mode", "form_post")
	                    .build()
	                    .toUri();
	            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
	            exchange.getResponse().getHeaders().setLocation(loginUri);
	            return exchange.getResponse().setComplete();
	        }
	    };
	}

	public static class Config {

	}
}
