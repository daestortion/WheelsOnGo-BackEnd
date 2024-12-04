package com.respo.respo.Configuration;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

    @PostConstruct
    public void init() {
        // Set default time zone to Asia/Manila
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Manila"));
    }
}