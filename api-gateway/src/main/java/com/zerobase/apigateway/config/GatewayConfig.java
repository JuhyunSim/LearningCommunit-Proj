package com.zerobase.apigateway.config;

import com.zerobase.apigateway.filter.AuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthorizationFilter authorizationFilter;

    @Bean
    public GlobalFilter customAuthorizationFilter() {
        return authorizationFilter;
    }
}

