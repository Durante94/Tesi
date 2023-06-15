package com.fabrizio.tesi.gateway.filters;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.fabrizio.tesi.gateway.filter.CheckAuth;

@SpringBootTest
public class CheckAuthTest {

    @Test
    void emptySession() {
        CheckAuth filter = new CheckAuth();

        filter.apply(new CheckAuth.Config());
    }
}
