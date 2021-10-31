package com.kdt.localinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LocalInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalInfoApplication.class, args);
	}

}
