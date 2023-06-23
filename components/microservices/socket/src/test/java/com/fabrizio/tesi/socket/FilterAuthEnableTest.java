package com.fabrizio.tesi.socket;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fabrizio.tesi.socket.filters.AuthFilter;
import com.fabrizio.tesi.socket.filters.MutableHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"auth.enable=true", "auth.secret=secret"})
public class FilterAuthEnableTest {
    @Value("${auth.secret}")
    String secret;
    @Autowired
    AuthFilter filter;

    @Test
    void noAuthHeaderTest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);
    }

    @Test
    void emptyAuthHeaderTest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        request.addHeader("Authorization", "");

        filter.doFilter(request, response, chain);
    }

    @Test
    void validAuthHeaderNoDataTest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create().sign(algorithm);
        request.addHeader("Authorization", token);

        filter.doFilter(request, response, chain);

        assertTrue(chain.getRequest() instanceof MutableHttpServletRequest);

        MutableHttpServletRequest customRequest = (MutableHttpServletRequest) chain.getRequest();

        assertNull(customRequest.getHeader("role"));
        assertNull(customRequest.getHeader("userName"));
    }

    @Test
    void validAuthHeaderWithDataTest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        String mockRole = "ruolo", mockUserName = "user";

        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                .withClaim("role", mockRole)
                .withClaim("preferred_username", mockUserName)
                .sign(algorithm);
        request.addHeader("Authorization", token);

        filter.doFilter(request, response, chain);

        assertTrue(chain.getRequest() instanceof MutableHttpServletRequest);

        MutableHttpServletRequest customRequest = (MutableHttpServletRequest) chain.getRequest();

        assertNotNull(customRequest.getHeader("role"));
        assertNotNull(customRequest.getHeader("userName"));
        assertEquals(mockRole, customRequest.getHeader("role"));
        assertEquals(mockUserName, customRequest.getHeader("userName"));
    }

    @Test
    void validAuthHeaderWithDataAndErrorTest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = Mockito.mock(MockFilterChain.class);
        String mockRole = "ruolo", mockUserName = "user";

        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                .withClaim("role", mockRole)
                .withClaim("preferred_username", mockUserName)
                .sign(algorithm);
        request.addHeader("Authorization", token);

        Mockito.doThrow(new IOException())
                .when(chain)
                .doFilter(Mockito.any(ServletRequest.class), Mockito.any(HttpServletResponse.class));

        filter.doFilter(request, response, chain);

        assertFalse(chain.getRequest() instanceof MutableHttpServletRequest);
    }
}
