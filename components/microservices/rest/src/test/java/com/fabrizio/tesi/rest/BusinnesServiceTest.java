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

@SpringBootTest(properties = {"businness.manager.url=http://localhost:50000", "businness.manager.initialdelay=999999999"})
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
		String[] responses = new String[] { "", "[\"test\"]" };
		int statusCodes[] = new int[] { HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.OK.value() };

		for (int i = 0; i < responses.length; i++) {
			mockBackEnd.enqueue(new MockResponse().setResponseCode(statusCodes[i])
					.addHeader("Content-Type", "application/json").setBody(responses[i]));
			List<String> agents = service.agentsList();
			if (i == responses.length - 1)
				assertFalse(agents.isEmpty());
			else
				assertTrue(agents.isEmpty());
		}
	}
}
