package com.eventhub.backend.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // CORS configuration for local development and deployed frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://eventhub-frontend-eta.vercel.app"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Spring Security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(jwtService);

        http
            .csrf(csrf -> csrf.disable())

            // Enable the CORS configuration defined above
            .cors(cors -> {})

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Login and signup
                .requestMatchers("/api/auth/**").permitAll()

                // User management: ADMIN only
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

                // Event creation: ADMIN or ORGANIZER
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/events",
                        "/api/events/"
                ).hasAnyRole("ADMIN", "ORGANIZER")

                // Event status changes: ADMIN only
                // This rule must appear before the general PUT rule
                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/events/*/status"
                ).hasRole("ADMIN")

                // Event editing: ADMIN or ORGANIZER
                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/events/*"
                ).hasAnyRole("ADMIN", "ORGANIZER")

                // Event deletion: ADMIN or ORGANIZER
                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/events/*"
                ).hasAnyRole("ADMIN", "ORGANIZER")

                // Registration creation: STUDENT only
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/registrations/*/*"
                ).hasRole("STUDENT")

                // Viewing registrations: authenticated roles
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/registrations/**"
                ).hasAnyRole(
                        "STUDENT",
                        "ORGANIZER",
                        "ADMIN"
                )

                // Registration cancellation: STUDENT only
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