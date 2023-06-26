package com.fabrizio.tesi.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:config/auth.properties")
class GatewayApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void coverageTest() {
		GatewayApplication.main(new String[] {});
	}
}
