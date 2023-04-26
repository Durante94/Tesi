package com.fabrizio.tesi.configurationscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ConfigurationSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigurationSchedulerApplication.class, args);
	}

}
