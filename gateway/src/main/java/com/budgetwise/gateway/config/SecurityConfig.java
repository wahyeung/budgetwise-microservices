package com.budgetwise.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain budgetSecurityFilterChain(HttpSecurity http) throws Exception {
        //Disable CSRF - Not required for stateless REST APIs
        http.csrf(csrf -> csrf.disable());

        //Secure all endpoints: authentication is mandatory
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll() // Allow public access to health checks
                .anyRequest().authenticated()
        );
        // Enable HTTP Basic Auth (Useful for testing with tools like Postman)
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        // In-memory user management for demonstration/development purposes
        var user = User.withUsername("luna")
                .password(passwordEncoder().encode("password123"))
                .roles("USER")
                .build();

        var admin = User.withUsername("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        // Use BCrypt strong hashing function for password encryption
        return new BCryptPasswordEncoder();
    }
}
