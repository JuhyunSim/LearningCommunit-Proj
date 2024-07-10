package com.zerobase.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {
    @Mock
    private BlackList blackList;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutService logoutService;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
    }

    @Test
    void logout_success() {
        // given
        when(request.getHeader("Refresh")).thenReturn("Bearer validRefreshToken");

        // when
        logoutService.logout(request, response, authentication);

        // then
        verify(request, times(1)).getSession();
        verify(request, times(1)).getHeader("Refresh");
        verify(blackList, times(1)).add("validRefreshToken");
        verify(authentication, never()).setAuthenticated(false);
    }
}