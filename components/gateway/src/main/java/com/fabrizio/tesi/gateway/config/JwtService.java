package com.fabrizio.tesi.gateway.config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fabrizio.tesi.gateway.auth.KeycloakTokenHandler;
import com.fabrizio.tesi.gateway.auth.KeycloakTokenRefresher;
import com.fabrizio.tesi.gateway.auth.KeycloakTokenRetriever;
import com.fabrizio.tesi.gateway.auth.exception.WamsAuthenticationException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtService {

	private static final String SESSION_ATTRIBUTE_AUTHORIZATION = "Authorization",
			SESSION_ATTRIBUTE_REFRESH = "RefreshAuth";

	@Value("${auth.secret}")
	String secret;

	@Value("${keycloack.clientid}")
	String clientId;

	@Value("${auth.enable}")
	Boolean authEnabled;

	@Value("${auth.mockrole}")
	String mockRole;

	@Value("${auth.showjwt}")
	Boolean showJWT;

	@Autowired
	KeycloakTokenRetriever tokenRetriever;

	@Autowired
	KeycloakTokenRefresher tokenRefresher;

	public Boolean checkTokenState(WebSession session) {
		if (!session.getAttributes().containsKey(SESSION_ATTRIBUTE_AUTHORIZATION)) {
			if (!authEnabled) {
				Builder JWTbuilder = JWT.create();

				Calendar customExpire = Calendar.getInstance();
				customExpire.set(Calendar.YEAR, customExpire.get(Calendar.YEAR) + 1);
				JWTbuilder.withClaim("role", mockRole);
				JWTbuilder.withClaim("preferred_username", "mockUser");
				JWTbuilder.withExpiresAt(customExpire.getTime());
				JWTbuilder.withIssuedAt(new Date());

				try {
					session.getAttributes().put(SESSION_ATTRIBUTE_AUTHORIZATION,
							JWTbuilder.sign(Algorithm.HMAC256(secret)));
				} catch (IllegalArgumentException | JWTCreationException | UnsupportedEncodingException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}

			return false;
		}

		String ts = session.getAttribute(SESSION_ATTRIBUTE_AUTHORIZATION);
		if (StringUtils.isBlank(ts))
			return false;

		DecodedJWT decodedJWT = JWT.decode(ts);

		return new Date().compareTo(decodedJWT.getExpiresAt()) <= 0;
	}

	public ResponseEntity<Integer> auth(WebSession session, String code) throws WamsAuthenticationException {
		try {
			return new ResponseEntity<>(storeJWTInSession(tokenRetriever, session, code), HttpStatus.OK);
		} catch (WamsAuthenticationException e) {
			if (e.isMissingRoles())
				throw e;
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	public ResponseEntity<Integer> refresh(WebSession session) throws WamsAuthenticationException {
		String refreshToken = retrieveToken(session);
		if (refreshToken == null || StringUtils.isBlank(refreshToken)) {
			log.error("No Session attribute: \"" + SESSION_ATTRIBUTE_REFRESH + "\"");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			return new ResponseEntity<>(storeJWTInSession(tokenRefresher, session, refreshToken), HttpStatus.OK);
		} catch (WamsAuthenticationException e) {
			if (e.isMissingRoles())
				throw e;
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void logout(WebSession session) {
		String refreshToken = retrieveToken(session);
		if (StringUtils.isBlank(refreshToken)) {
			log.error("No Session attribute: \"" + SESSION_ATTRIBUTE_REFRESH + "\". Logout requested, no worries");
			return;
		}

		try {
			tokenRefresher.deleteToken(refreshToken);
		} catch (IOException e) {
			log.error("Logout request: cannot delete token");
		}
	}

	private String retrieveToken(WebSession session) {
		return session.getAttribute(SESSION_ATTRIBUTE_REFRESH);
	}

	private int storeJWTInSession(KeycloakTokenHandler handler, WebSession session, String value)
			throws WamsAuthenticationException {
		String response;
		try {
			response = handler.getToken(value);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new WamsAuthenticationException(true);
		}

		if (showJWT)
			log.info("JWT: " + response);
		Gson gson = new Gson();
		JsonObject responseSet = gson.fromJson(response, JsonObject.class);

		Map<String, Claim> accessTokenClaims = (JWT.decode(responseSet.get("access_token").getAsString()).getClaims());
		// NECESSARIO PER PERMETTERE DI CANCELLARE LA SESSIONE CON KEYCLOACK E RICREARLA
		// NEL CASO DI MANCANZA DI RUOLI
		session.getAttributes().put(SESSION_ATTRIBUTE_REFRESH, responseSet.get("refresh_token").getAsString());
		List<String> roles;

		try {
			roles = List
					.of(gson.fromJson(accessTokenClaims.get("resource_access").asMap().get(clientId).toString(),
							JsonObject.class).get("roles").getAsJsonArray())
					.stream().map(JsonElement::getAsString).collect(Collectors.toList());
		} catch (Exception e) {
			throw new WamsAuthenticationException(true);

		}
		boolean isAdmin = roles.contains("admin"), isAnalist = roles.contains("analist");

		DecodedJWT decodedJWT = JWT.decode(responseSet.get("id_token").getAsString());

		// Getting maps for payload and header from decompiled JWT
		Map<String, Object> header = new HashMap<>();
		Map<String, Claim> payload = decodedJWT.getClaims();

		header.put("alg", decodedJWT.getHeaderClaim("alg"));
		header.put("typ", decodedJWT.getHeaderClaim("typ"));

		// Building new JWT with retrieved one.

		String jwtToken;
		int validitySpan = responseSet.get("expires_in").getAsInt();
		try {
			Builder JWTbuilder = JWT.create();

			payload.forEach((k, v) -> JWTbuilder.withClaim(k, v.asString()));

			if (isAdmin)
				JWTbuilder.withClaim("role", "ADMIN");
			else if (isAnalist)
				JWTbuilder.withClaim("role", "USER");
			// SE NON HO UN RUOLO, LANCIO ECCEZIONE PER REDIRECT A PAGINA DEDICATA
			else
				throw new WamsAuthenticationException(true);

			JWTbuilder.withHeader(header);
			JWTbuilder.withKeyId(decodedJWT.getKeyId());
			JWTbuilder.withExpiresAt(decodedJWT.getExpiresAt());
			JWTbuilder.withIssuedAt(decodedJWT.getIssuedAt());

			jwtToken = JWTbuilder.sign(Algorithm.HMAC256(secret));
		} catch (JWTCreationException e) {
			log.error("Error creating token for refresh");
			throw new WamsAuthenticationException();
		} catch (IllegalArgumentException | UnsupportedEncodingException e) {
			log.error(e.getMessage());
			throw new WamsAuthenticationException();
		}
		session.getAttributes().put(SESSION_ATTRIBUTE_AUTHORIZATION, jwtToken);
		return validitySpan;
	}
}