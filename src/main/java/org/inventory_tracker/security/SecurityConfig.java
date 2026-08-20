package org.inventory_tracker.security;

import lombok.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CamsAuthenticationFilter camsAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/",
                                "/api/v1/terminals/session",
                                "/api/v1/pump-audits/shift-summary",
                                "/api/v1/station-inventories/price",
                                "/api/v1/sales",
                                "/api/v1/pump-audits/close"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(camsAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
