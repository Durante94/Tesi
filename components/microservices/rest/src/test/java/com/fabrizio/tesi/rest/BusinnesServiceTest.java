package com.fabrizio.tesi.rest;

import com.fabrizio.tesi.rest.services.BusinessService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {"businness.manager.url=http://localhost:50000"})
@ActiveProfiles("dev")
@FieldDefaults(level = AccessLevel.PRIVATE)
class BusinnesServiceTest {
    static MockWebServer mockBackEnd;

    @Autowired
    BusinessService service;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(50000);
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        mockBackEnd.shutdown();
    }

    @Test
    void responseTest() {
        MockResponse[] responses = new MockResponse[]{
                new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody("[]"),
                new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody("[\"test\"]")
        };
        for (int i = 0; i < responses.length; i++) {
            mockBackEnd.enqueue(responses[i]);

            List<String> agents = service.agentsList();
            if (i == responses.length - 1)
                assertFalse(agents.isEmpty());
            else
                assertTrue(agents.isEmpty());
        }
    }
}
