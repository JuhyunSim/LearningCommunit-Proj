package com.zerobase.apigateway.filter;

import com.zerobase.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private static final List<String> EXCLUDE_PATHS =
            List.of(
                    "/users/login",
                    "/users/register",
                    "/login/**",
                    "/login",
                    "/"
            );
    private final String ACCESS_HEADER = "Authorization";
    private final String PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        log.debug("request path: {}", path);

        if (EXCLUDE_PATHS.contains(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(ACCESS_HEADER);
        if (authHeader == null || !authHeader.startsWith(PREFIX)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            log.info("Unauthorized Authorization Header: {}", authHeader);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(PREFIX.length());
        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            log.info("Unauthorized Token: {}", token);
            return exchange.getResponse().setComplete();
        }

        // 기존 헤더 값이 존재하는지 확인하고, 없을 때만 추가
        exchange = exchange.mutate().request(r -> r.headers(headers -> {
            if (!headers.containsKey("Authorization")) {
                headers.add("Authorization", token);
            }
        })).build();

        // 비동기로 authentication 호출
        Authentication authentication = jwtUtil.getAuthentication(token);
        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                .doOnSuccess(aVoid -> log.debug("Successfully set SecurityContext"));
    }


    @Override
    public int getOrder() {
        return 0; // spring security 이후로 동작
    }
}
