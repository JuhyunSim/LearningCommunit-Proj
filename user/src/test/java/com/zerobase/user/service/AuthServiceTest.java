package com.zerobase.user.service;

import com.zerobase.common.dto.JwtResponse;
import com.zerobase.user.dto.LoginForm;
import com.zerobase.common.exception.CustomException;
import com.zerobase.common.exception.ErrorCode;
import com.zerobase.common.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityMemberService securityMemberService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_success() throws Exception {
        // given
        String username = "testUser";
        String password = "password";
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(username);
        loginForm.setPassword(password);

        UserDetails userDetails = mock(UserDetails.class);
        List<GrantedAuthority> authorities =
                new ArrayList<>(userDetails.getAuthorities());
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager
                .authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(securityMemberService.loadUserByUsername(username))
                .thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(jwtUtil.generateToken(username, authorities)).thenReturn("accessJwt");
        when(jwtUtil.generateRefreshToken(username)).thenReturn("refreshJwt");

        // when
        JwtResponse result = authService.authenticate(loginForm);

        // then
        assertEquals("accessJwt", result.getAccessJwt());
        assertEquals("refreshJwt", result.getRefreshJwt());
    }

    @Test
    public void testAuthenticateFailure() {
        // given
        String username = "testUser";
        String password = "password";
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(username);
        loginForm.setPassword(password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Iznvalid login credentials") {});

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.authenticate(loginForm);
        });

        // then
        assertEquals(ErrorCode.INVALID_LOGIN, exception.getErrorCode());
        assertEquals("잘못된 아이디 또는 비밀번호입니다.", exception.getMessage());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityMemberService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyList());
        verify(jwtUtil, never()).generateRefreshToken(anyString());
    }

    @Test
    public void refreshToken_Success() throws Exception {
        // given
        String accessToken = "validAccessToken";
        String refreshToken = "validRefreshToken";
        String username = "testUser";
        List<GrantedAuthority> authorities = new ArrayList<>();

        UserDetails userDetails = mock(UserDetails.class);

        when(jwtUtil.extractUsername(accessToken)).thenReturn(username);
        when(jwtUtil.validateToken(username, refreshToken)).thenReturn(true);
        when(securityMemberService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.generateToken(username, authorities)).thenReturn("newAccessToken");
        // when
        JwtResponse jwtResponse = authService.refreshToken(accessToken, refreshToken);

        // then
        assertNotNull(jwtResponse);
        assertEquals("newAccessToken", jwtResponse.getAccessJwt());
        assertEquals(refreshToken, jwtResponse.getRefreshJwt());

        verify(jwtUtil, times(1)).extractUsername(accessToken);
        verify(jwtUtil, times(1)).validateToken(username, refreshToken);
        verify(securityMemberService, times(1)).loadUserByUsername(username);
        verify(jwtUtil, times(1)).generateToken(username, authorities);
    }


    @Test
    public void testRefreshTokenFailure() {
        // given
        String accessToken = "validAccessToken";
        String refreshToken = "invalidRefreshToken";
        String username = "testUser";

        when(jwtUtil.extractUsername(accessToken)).thenReturn(username);
        when(jwtUtil.validateToken(username, refreshToken)).thenReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.refreshToken(accessToken, refreshToken);
        });

        // then
        assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());

        verify(jwtUtil, times(1)).extractUsername(accessToken);
        verify(jwtUtil, times(1)).validateToken(username, refreshToken);
        verify(securityMemberService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyList());
    }
}