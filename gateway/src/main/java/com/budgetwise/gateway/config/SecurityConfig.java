package com.budgetwise.gateway.config;

import com.budgetwise.gateway.security.GatewayJwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final GatewayJwtFilter gatewayJwtFilter;

    @Bean
    public SecurityFilterChain budgetwiseSecurityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for stateless REST APIs
        http.csrf(csrf -> csrf.disable());
        // Configure URL authorization
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll() // Publicly accessible endpoints
                .anyRequest().authenticated() // All other requests require authentication
        );
        // Enforce stateless session management (No Session created or used)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Custom Error Handling: Return 401 instead of redirecting to a login page
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                        response.sendError(401, "Unauthorized")));

        // Place our custom JWT filter BEFORE the standard username/password filter
        http.addFilterBefore(gatewayJwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}