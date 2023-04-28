package com.algowatt.wams.auth;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeycloakTokenRefresher extends KeycloakTokenHandler {

	@Override
	protected String getGrantType() {
		return "refresh_token";
	}

	@Override
	protected void setPostDataParam(String value) {
		this.postData = "refresh_token=" + value;
	}

}
