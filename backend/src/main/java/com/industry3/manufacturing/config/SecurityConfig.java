package com.industry3.manufacturing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Allow unrestricted access to Swagger UI and API docs
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // Allow unrestricted access to API endpoints for development
                .requestMatchers("/api/**").permitAll()
                // Allow access to actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                // Allow access to H2 database console (if used)
                .requestMatchers("/h2-console/**").permitAll()
                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API testing
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable()) // Allow H2 console frames
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
