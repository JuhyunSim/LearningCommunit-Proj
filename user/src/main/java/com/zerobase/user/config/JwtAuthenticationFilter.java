package com.zerobase.user.config;

import com.zerobase.user.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    //본인의 토큰이 맞고 유효기간이 지나지 않았을 때
    // 토큰이 없거나 유효하지 않은 경우 필터 체인으로 넘깁니다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String jwtToken = resolveToken(request);
        if (jwtToken != null && jwtUtil.validateToken(jwtToken, jwtUtil.extractUsername(jwtToken))) {
            Authentication authentication = jwtUtil.getAuthentication(jwtToken);
            log.info("Filtering request token Authentication: {}", authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info(String.format("[%s] -> %s ",
                    jwtUtil.extractUsername(jwtToken), request.getRequestURI())
            );
        }
        log.info("Filtering request token: {}", jwtToken);
        log.info("authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}


