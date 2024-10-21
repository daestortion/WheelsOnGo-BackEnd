package com.respo.respo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class MyCorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        
        // Allow specific origins (add Railway and Vercel domains)
        corsConfig.addAllowedOriginPattern("https://*.vercel.app");
        corsConfig.addAllowedOriginPattern("https://*.railway.app");
        corsConfig.addAllowedOriginPattern("http://localhost:3000");  // Keep localhost for development
        corsConfig.addAllowedOriginPattern("https://api.paymongo.com/v1/links");

        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");  // Allow all HTTP methods (GET, POST, etc.)
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsFilter(source);
    }
}
