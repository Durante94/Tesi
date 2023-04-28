package com.fabrizio.tesi.gateway.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@PropertySource("classpath:config/auth.properties")
@Configuration
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AuthConfigurationProprieties {

	String secret;
	String code;
	String login_url;
	String token_url;
	String clientid;
	String redirect_uri;
	String response_type;
	String scope;
	String auth_code;
	String refresh;
	String app_url;
}
