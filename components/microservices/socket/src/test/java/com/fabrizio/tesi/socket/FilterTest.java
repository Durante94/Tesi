package com.fabrizio.tesi.socket;

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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FilterTest {
    @Value("${auth.mockRole}")
    String mockRole;
    @Autowired
    AuthFilter filter;

    @Test
    void emptyHeaderTest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertTrue(chain.getRequest() instanceof MutableHttpServletRequest);

        MutableHttpServletRequest customRequest = (MutableHttpServletRequest) chain.getRequest();

        assertNotNull(customRequest.getHeader("role"));
        assertEquals(mockRole, customRequest.getHeader("role"));
    }

    @Test
    void emptyHeaderWithChainErrorTest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = Mockito.mock(MockFilterChain.class);

        Mockito.doThrow(new IOException())
                .when(chain)
                .doFilter(Mockito.any(ServletRequest.class), Mockito.any(HttpServletResponse.class));

        filter.doFilter(request, response, chain);


        assertFalse(chain.getRequest() instanceof MutableHttpServletRequest);
    }
}
