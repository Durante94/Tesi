package com.fabrizio.tesi.rest;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import com.auth0.jwt.algorithms.Algorithm;
import com.fabrizio.tesi.rest.filters.AuthFilter;

@SpringBootTest(properties = { "auth.enable=true" })
@AutoConfigureTestDatabase
@ActiveProfiles("dev")
class AuthFiltersTestAuthEnable {
	@Autowired
	AuthFilter filter;

	@Test
	void emptyHeader() throws ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		filter.doFilter(request, response, chain);
	}

	@Test
	void invalidToken() throws ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		request.addHeader("Authorization", "");

		filter.doFilter(request, response, chain);
	}

//	@Test
	void nullSecret() throws ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		request.addHeader("Authorization", "");

		try (MockedStatic<Algorithm> utilities = Mockito.mockStatic(Algorithm.class)) {
			utilities.when(() -> Algorithm.HMAC256(Mockito.anyString())).thenThrow(IllegalArgumentException.class);

			filter.doFilter(request, response, chain);
		}
	}
}
