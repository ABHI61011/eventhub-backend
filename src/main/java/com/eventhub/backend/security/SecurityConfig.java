package com.eventhub.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(jwtService);

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> {})

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Login and signup
                .requestMatchers("/api/auth/**").permitAll()

                // User management: admin only
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/users",
                    "/api/users/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/users/**"
                ).hasRole("ADMIN")

                // Public event and category viewing
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/events",
                    "/api/events/**",
                    "/api/categories",
                    "/api/categories/**"
                ).permitAll()

                // Event creation: admin or organizer
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/events",
                    "/api/events/"
                ).hasAnyRole("ADMIN", "ORGANIZER")

                // Event editing: admin or organizer
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/events/*"
                ).hasAnyRole("ADMIN", "ORGANIZER")

                // Event status changes: admin only
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/events/*/status"
                ).hasRole("ADMIN")

                // Event deletion: admin or organizer
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/events/*"
                ).hasAnyRole("ADMIN", "ORGANIZER")

                // Registration creation: students only
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/registrations/*/*"
                ).hasRole("STUDENT")

                // Viewing registration information requires login
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/registrations/**"
                ).hasAnyRole("STUDENT", "ORGANIZER", "ADMIN")

                // Cancellation requires a logged-in student
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/registrations/*"
                ).hasRole("STUDENT")

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}