package com.respo.respo;

import java.util.TimeZone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class RespoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RespoApplication.class, args);
		System.out.println("HUMANA ME CAPSTONE 1!!!!!!!!!!!!!!!!!!");
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        System.out.println("Initial Heap Size: " + heapMemoryUsage.getInit() / (1024 * 1024) + " MB");
        System.out.println("Max Heap Size: " + heapMemoryUsage.getMax() / (1024 * 1024) + " MB");
        System.out.println("Used Heap Size: " + heapMemoryUsage.getUsed() / (1024 * 1024) + " MB");
	}
	
	@Override
    public void run(String... args) throws Exception {
        // Set the default time zone globally to Asia/Manila
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Manila"));
    }
}