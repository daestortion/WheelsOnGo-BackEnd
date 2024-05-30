package com.respo.respo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class RespoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RespoApplication.class, args);
		System.out.println("Way Forever!");
	}

}