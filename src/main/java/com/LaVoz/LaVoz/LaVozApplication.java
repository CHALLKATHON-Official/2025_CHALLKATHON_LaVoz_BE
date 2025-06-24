package com.LaVoz.LaVoz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LaVozApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaVozApplication.class, args);
	}

}
