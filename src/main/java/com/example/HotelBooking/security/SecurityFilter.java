package com.example.HotelBooking.security;

import com.example.HotelBooking.exceptions.CustomAccessDenialHandler;
import com.example.HotelBooking.exceptions.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Marks this class as a Spring config class
@EnableWebSecurity // Enables Spring Security
@EnableMethodSecurity // Allows method-level security (like @PreAuthorize)
@RequiredArgsConstructor // Auto-generates constructor for final fields (DI)
@Slf4j // Logger
public class SecurityFilter {

    // Inject custom JWT filter
    private final AuthFilter authFilter;

    // Handle 403 Forbidden errors
    private final CustomAccessDenialHandler customAccessDenialHandler;

    // Handle 401 Unauthorized errors
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // Define the filter chain for Spring Security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless JWT
                .cors(Customizer.withDefaults()) // Enable CORS

                // Custom exception handling
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDenialHandler) // 403 handler
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 401 handler
                )

                // Define which endpoints are public vs authenticated
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/auth/**", "/api/rooms/**", "/api/bookings/**").permitAll() // Public
                        .anyRequest().authenticated() // All others require auth
                )

                // Set session management to stateless (JWT does not store sessions)
                .sessionManagement(manager -> manager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Add our custom JWT filter BEFORE Spring's UsernamePasswordAuthenticationFilter
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build(); // Build the security chain
    }

    // Password encoder bean for hashing passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager bean for login flow
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
