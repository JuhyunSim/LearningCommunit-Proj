package com.zerobase.user.controller;

import com.zerobase.user.service.LogoutService;
import com.zerobase.user.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LogoutController {
    private final LogoutService logoutService;
    private final JwtUtil jwtUtil;

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> logout(
            @RequestHeader(name = "Authorization") String accessToken,
            @RequestHeader(name = "Refresh") String refreshToken,
            HttpServletRequest request
    ) {
        log.debug("logout controller start!!!!");
        log.debug("accessToken: {}", accessToken);
        log.debug("request access token == accesstoken: {}", request.getHeader("Authorization").equals(accessToken));
        log.debug("refreshToken: {}", refreshToken);
        log.debug("request refresh token == refreshtoken: {}", request.getHeader("Refresh").equals(refreshToken));

        logoutService.logout(request, null, null);
        return ResponseEntity.ok().build();
    }
}
