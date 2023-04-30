package com.fabrizio.tesi.gateway.test;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class TestEnv {
    @Autowired
    Environment env;

    @PostConstruct
    void log() {
        log.info("ROUTES: ", env.getProperty("spring.cloud.gateway.routes"));
    }
}
