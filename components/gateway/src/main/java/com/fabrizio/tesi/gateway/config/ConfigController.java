package com.fabrizio.tesi.gateway.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fabrizio.tesi.gateway.auth.exception.WamsAuthenticationException;
import com.fabrizio.tesi.gateway.dto.CallbackRequest;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = { "/gateway" })
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ConfigController {

	@Autowired
	JwtService jwtService;

	@Value("${keycloack.feingress}")
	String entryPoint;

	@PostMapping("/auth")
	public void getAuth(WebSession session, @ModelAttribute CallbackRequest reqCallBack, ServerHttpResponse response) {
		String location = "/web/";
		try {
			jwtService.auth(session, reqCallBack.getCode());
		} catch (WamsAuthenticationException e) {
			if (e.isMissingRoles())
				location = "/index.html";
		} catch (NullPointerException | JWTDecodeException e) {
			log.error("Invalid response from OAuth provider", e);
			location = "/index.html";
		}
		redirect(response, location);
	}

	@GetMapping("/auth/refresh")
	public ResponseEntity<Integer> refreshJwt(WebSession session, ServerHttpRequest request,
			ServerHttpResponse response) {
		try {
			return jwtService.refresh(session);
		} catch (WamsAuthenticationException e) {
			if (e.isMissingRoles()) {
				return new ResponseEntity<>(0, HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (NullPointerException | JWTDecodeException e) {
			log.error("Invalid response from OAuth provider", e);
			return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/auth/logout")
	public void destroySession(WebSession session, ServerHttpResponse response) {
		doLogout(session);
		response.setStatusCode(HttpStatus.FOUND);
	}

	@GetMapping("/auth")
	public void reDoLogin(WebSession session, ServerHttpResponse response) {
		doLogout(session);
		redirect(response, "/web/");
	}

	private void doLogout(WebSession session) {
		jwtService.logout(session);
		session.getAttributes().clear();
	}

	private void redirect(ServerHttpResponse response, String location) {
		response.getHeaders().setLocation(URI.create(entryPoint + location));
		response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
	}

	@GetMapping("/auth/cypress")
	public ResponseEntity<Boolean> authenticated(WebSession session) {
		return new ResponseEntity<>(jwtService.checkTokenState(session), HttpStatus.OK);
	}
}
