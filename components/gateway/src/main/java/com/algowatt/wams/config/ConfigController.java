package com.algowatt.wams.config;

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

import com.algowatt.wams.auth.exception.MyAuthenticationException;
import com.algowatt.wams.dto.CallbackRequest;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RequestMapping(value = { "/gateway" })
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigController {

	@Autowired
	JwtService jwtService;

	@Value("${keycloack.feingress}")
	String entryPoint;

	@PostMapping("/auth")
	public void getAuth(WebSession session, @ModelAttribute CallbackRequest reqCallBack, ServerHttpResponse response) {
		String location = "/mpdc/";
		try {
			jwtService.auth(session, reqCallBack.getCode());
		} catch (MyAuthenticationException e) {
			if (e.isMissingRoles())
				location = "/index.html";
		}
		redirect(response, location);
	}

	@GetMapping("/auth/refresh")
	public ResponseEntity<Integer> refreshJwt(WebSession session, ServerHttpRequest request,
			ServerHttpResponse response) {
		try {
			return jwtService.refresh(session);
		} catch (MyAuthenticationException e) {
			if (e.isMissingRoles()) {
				return new ResponseEntity<>(0, HttpStatus.UNAUTHORIZED);
			}
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
		redirect(response, "/mpdc/");
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
