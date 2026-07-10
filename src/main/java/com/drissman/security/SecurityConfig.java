package com.drissman.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/api/health").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/schools/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/offers/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/images/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/training-periods/published/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/documents/upload").hasAnyRole("MONITOR", "SCHOOL_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/documents/**").hasAnyRole("MONITOR", "SCHOOL_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/v1/documents/**").authenticated()
                        .pathMatchers("/api/schools/admin/**").hasRole("SCHOOL_ADMIN")
                        .pathMatchers("/api/monitors/**").hasAnyRole("MONITOR", "SCHOOL_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/registrations").hasRole("VISITOR")
                        .pathMatchers(HttpMethod.GET, "/api/registrations/me").hasRole("VISITOR")
                        .pathMatchers("/api/admin/registrations/**").hasRole("SCHOOL_ADMIN")
                        .pathMatchers("/api/enrollments/**").hasAnyRole("VISITOR", "STUDENT", "SCHOOL_ADMIN")
                        .anyExchange().authenticated())
                .build();
    }
}
