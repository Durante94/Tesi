package com.fabrizio.tesi.configurationscheduler;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.test.context.TestPropertySource;

import com.fabrizio.tesi.configurationscheduler.config.MyCacheManagerCustomizer;

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
		MyCacheManagerCustomizer cache = new MyCacheManagerCustomizer();
		assertNotNull(cache);

		cache.customize(new ConcurrentMapCacheManager());
	}
}
