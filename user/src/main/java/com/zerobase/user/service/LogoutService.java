package com.zerobase.user.service;

import com.zerobase.common.service.BlackList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {
    private final BlackList blackList;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        String refreshToken = request
                .getHeader("Refresh");
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring("Bearer ".length());
            blackList.add(refreshToken);
            log.info("Refresh token added to blacklist: {}", refreshToken);
        } else {
            log.warn("No valid refresh token found in the request headers.");
        }
    }
}
