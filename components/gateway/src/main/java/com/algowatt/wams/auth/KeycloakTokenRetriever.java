package com.algowatt.wams.auth;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeycloakTokenRetriever extends KeycloakTokenHandler {

	@Override
	protected void setPostDataParam(String value) {
		this.postData = "code=" + value;
	}

	@Override
	protected String getGrantType() {
		return "authorization_code";
	}
}
