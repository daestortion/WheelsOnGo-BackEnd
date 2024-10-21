package com.respo.respo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class MyCorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);  // Allow credentials (cookies)
        corsConfig.addAllowedOriginPattern("*");  // Allow all origins for now (or be more specific)
        
        // Specify allowed origins, including subdomains, for Railway, Vercel, and localhost
        corsConfig.setAllowedOriginPatterns(List.of(
            "https://*.vercel.app",    // Vercel deployment
            "https://*.railway.app",   // Railway deployment
            "http://localhost:3000"    // Localhost during development
        ));

        corsConfig.addAllowedHeader("*");  // Allow all headers
        corsConfig.addAllowedMethod("*");  // Allow all HTTP methods (GET, POST, etc.)
        
        // Register the CORS configuration for all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsFilter(source);
    }
}
