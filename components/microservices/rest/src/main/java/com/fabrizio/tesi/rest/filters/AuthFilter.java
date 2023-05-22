package com.fabrizio.tesi.rest.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(1)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AuthFilter extends OncePerRequestFilter {
    @Value("${auth.secret}")
    String secret;

    @Value("${auth.enable}")
    Boolean authEnabled;

    @Value("${auth.mockRole}")
    String mockRole;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

        if (!authEnabled) {
            mutableRequest.putHeader("role", mockRole);
            try {
                filterChain.doFilter(mutableRequest, response);
            } catch (IOException | ServletException e) {
                log.error("Filter error:", e);
                return;
            }
        }
        String token = request.getHeader("Authorization");

        if (token == null) {
            new HttpResponseMessages().returnAuthError(response);
            return;
        }

        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(secret);
        } catch (IllegalArgumentException | UnsupportedEncodingException e1) {
            log.error("Token problems", e1);
            new HttpResponseMessages().returnAuthError(response);
            return;
        }

        DecodedJWT jwt = null;

        try {
            JWTVerifier verifier = JWT.require(algorithm).build(); // Reusable verifier instance
            jwt = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            log.error("JWT", exception);
            new HttpResponseMessages().returnAuthError(response);
            return;
        }

        mutableRequest.putHeader("role", jwt.getClaim("role").asString());
        mutableRequest.putHeader("userName", jwt.getClaim("preferred_username").asString());

        try {
            filterChain.doFilter(mutableRequest, response);
        } catch (IOException | ServletException e) {
            log.error("Cannot elaborate request", e);
        }
    }
}