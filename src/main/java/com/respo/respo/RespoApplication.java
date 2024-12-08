package com.respo.respo;

import java.util.TimeZone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class RespoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RespoApplication.class, args);
		System.out.println("HUMANA ME CAPSTONE 1!!!!!!!!!!!!!!!!!!");
	}
	
	@Override
    public void run(String... args) throws Exception {
        // Set the default time zone globally to Asia/Manila
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Manila"));
    }
}