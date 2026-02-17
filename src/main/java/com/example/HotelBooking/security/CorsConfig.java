/*This class configures CORS (Cross-Origin Resource Sharing) so that your Spring Boot backend can accept requests from different origins (e.g., your frontend running on localhost:3000) without being blocked by the browser.*/
package com.example.HotelBooking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Marks this as a configuration class for Spring
public class CorsConfig {

    @Bean // Defines a Spring bean that will be managed by the Spring container
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            // Override addCorsMappings to define custom CORS rules
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply CORS rules to all endpoints
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow these HTTP methods
                        .allowedOrigins("*"); // Allow requests from any origin (frontend)
            }
        };
    }
}
/*
Without this, your frontend (React/Angular) might get CORS errors when calling your API.
This has nothing to do with JWT or login directly, but is required for the frontend/backend to communicate in a cross-origin setup.
Required for frontend to successfully call backend APIs; does not handle JWT, login, or authentication
CorsConfig: allows frontend to access backend from any origin. Configures allowed methods and applies globally.
 */