package com.example.HotelBooking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marks this as a Spring bean; Spring will automatically add it to the security filter chain
@Slf4j // Provides a logger named 'log' for logging errors/info
@RequiredArgsConstructor // Generates a constructor to inject final fields (jwtUtils & customUserDetailsService)
public class AuthFilter extends OncePerRequestFilter { // Runs once per request; ensures JWT authentication is applied

    // JWT utility to generate/validate/extract token
    private final JwtUtils jwtUtils;

    // Custom service to load user details from database
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Extract JWT token from Authorization header
        String token = getTokenFromRequest(request);

        log.info("TOKEN = {}", token); //

        if(token != null){ // Only process if token exists
            // Step 2: Extract email/username from JWT claims
            String email = jwtUtils.getUsernameFromToken(token);

            log.info("EMAIL FROM TOKEN = {}", email); //

            // Step 3: Load user details from DB and wrap it in AuthUser
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            log.info("TOKEN VALID = {}", jwtUtils.isTokenValid(token,userDetails));   //

            // Step 4: Validate token and check email is not empty
            if(StringUtils.hasText(email) && jwtUtils.isTokenValid(token,userDetails)) {

                // Step 5: Create authentication token for Spring Security
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Step 6: Add extra request info (like IP, session) to authentication object
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 7: Set the authenticated user in Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                log.info("AUTHENTICATION SET SUCCESSFULLY"); //
            }
        }

        try {
            // Step 8: Continue with next filter or controller
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // Log any unexpected errors in filter execution
            log.error(e.getMessage());
        }
    }

    // Helper method to extract token from Authorization header
    private String getTokenFromRequest(HttpServletRequest request) {
        // Get Authorization header value
        String tokenWithBearer = request.getHeader("Authorization");

        // Check if header exists and starts with "Bearer "
        if(tokenWithBearer != null && tokenWithBearer.startsWith("Bearer ")){
            // Remove "Bearer " prefix and return the token
            return tokenWithBearer.substring(7);
        }

        // Return null if no valid token is found
        return null;
    }
}
