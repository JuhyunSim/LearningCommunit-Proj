package com.zerobase.apigateway.config;


import com.zerobase.apigateway.service.CustomOAuth2SuccessHandler;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Base64;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomOAuth2SuccessHandler successHandler;

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        log.debug("Loaded JWT secret: {}", secret);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        log.debug("Loaded SecurityWebFilterChain");
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF 보호 비활성화
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/users/login/**",
                                "/users/register/**",
                                "/users/oauth2",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/csrf-token",
                                "/search/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2LoginSpec ->
                        oauth2LoginSpec
                                .authenticationSuccessHandler(successHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
                );
        return http.build();
    }

    @Bean
    public NimbusReactiveJwtDecoder jwtDecoder() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return NimbusReactiveJwtDecoder.withSecretKey(Keys.hmacShaKeyFor(keyBytes)).build();
    }
}