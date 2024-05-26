package com.respo.respo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
	    return new WebMvcConfigurer() {
	        @Override
	        public void addCorsMappings(CorsRegistry registry) {
	            registry.addMapping("/**")
	                    .allowedOrigins("https://localhost:3000") // Ensure this matches your client URL exactly
	                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
	                    .allowedHeaders("*")
	                    .allowCredentials(true)
	                    .exposedHeaders("Header1", "Header2") // Add headers that you want to expose to the client
	                    .maxAge(3600); // Max age for the cache of preflight response
	        }
	    };
	}

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests.anyRequest().permitAll() // This line changes the authorization to permit all requests.
            )
            .httpBasic().disable() // This disables HTTP Basic authentication.

            // Optionally disable CSRF protection if your service is purely stateless
            .csrf().disable();

        return http.build();
    }
}
