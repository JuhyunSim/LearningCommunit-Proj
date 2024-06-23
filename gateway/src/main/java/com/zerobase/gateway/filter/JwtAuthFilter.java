package com.zerobase.gateway.filter;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 비회원 접근 허용 경로 설정
        if (path.startsWith("/search") || path.startsWith("/public")) {
            return chain.filter(exchange);
        }

        // JWT 토큰 검증 로직
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean validateToken(String token) {
        // JWT 토큰 검증 로직 구현
        return true; // 예제에서는 항상 true 반환
    }
}