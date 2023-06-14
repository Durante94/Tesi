package com.fabrizio.tesi.configurationscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(properties = { "spring.profiles.active=dev" })
class ConfigurationSchedulerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void coverageTest() {
		ConfigurationSchedulerApplication.main(new String[] {});
	}
}
