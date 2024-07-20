package com.zerobase.user.service;

import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlackListTest {
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private BlackList blackList;

    @Test
    public void testAdd() {
        // given
        String refreshToken = "validRefreshToken";
        String refreshToken2 = "validRefreshToken2";
        Date expiryDate = new Date(System.currentTimeMillis() + 10000); // 10 초
        Claims claims = mock(Claims.class);
        Claims claims2 = mock(Claims.class);
        when(jwtUtil.extractAllClaims(refreshToken)).thenReturn(claims);
        when(jwtUtil.extractAllClaims(refreshToken2)).thenReturn(claims2);
        when(claims.getExpiration()).thenReturn(expiryDate);
        when(claims2.getExpiration()).thenReturn(expiryDate);

        // when
        blackList.add(refreshToken);
        blackList.add(refreshToken2);

        // then
        assertTrue(blackList.isListed(refreshToken));
        assertTrue(blackList.isListed(refreshToken2));
        verify(jwtUtil, times(1)).extractAllClaims(refreshToken);
        verify(claims, times(1)).getExpiration();
    }

    @Test
    void isListed_true() {
        // given
        String refreshToken = "validRefreshToken";
        Date expiryDate = new Date(System.currentTimeMillis() + 10000); // 10 seconds later
        Claims claims = mock(Claims.class);
        when(jwtUtil.extractAllClaims(refreshToken)).thenReturn(claims);
        when(claims.getExpiration()).thenReturn(expiryDate);
        blackList.add(refreshToken);

        // when
        boolean result = blackList.isListed(refreshToken);

        // then
        assertTrue(result);
    }

    @Test
    public void isListed_false() {
        // given
        String refreshToken = "nonListedToken";

        // when
        boolean result = blackList.isListed(refreshToken);

        // then
        assertFalse(result);
    }

    @Test
    public void isListed_null() {
        // when
        CustomException exception =
                assertThrows(
                        CustomException.class, () -> {
            blackList.isListed(null);
        });

        // then
        assertEquals(ErrorCode.NO_VALID_REFRESH_TOKEN, exception.getErrorCode());
    }

    @Test
    public void removeExpiredTokens_success() {
        // given
        String refreshToken = "expiredRefreshToken";
        Date expiryDate = new Date(System.currentTimeMillis() - 10000); // 10 seconds ago
        Claims claims = mock(Claims.class);
        when(jwtUtil.extractAllClaims(refreshToken)).thenReturn(claims);
        when(claims.getExpiration()).thenReturn(expiryDate);
        blackList.add(refreshToken);

        // when
        blackList.removeExpiredTokens();

        // then
        assertFalse(blackList.isListed(refreshToken));
    }

    @Test
    public void removeExpiredTokens_withTimeSleep() throws InterruptedException {
        // given
        String refreshToken = "expiredRefreshToken";
        String refreshToken2 = "validRefreshToken";
        Date expiredDate = new Date(System.currentTimeMillis() - 10000); // 10 seconds ago
        Date validDate = new Date(System.currentTimeMillis() + 10000); // 10 seconds later
        Claims expiredClaims = mock(Claims.class);
        Claims validClaims = mock(Claims.class);

        when(jwtUtil.extractAllClaims(refreshToken)).thenReturn(expiredClaims);
        when(jwtUtil.extractAllClaims(refreshToken2)).thenReturn(validClaims);
        when(expiredClaims.getExpiration()).thenReturn(expiredDate);
        when(validClaims.getExpiration()).thenReturn(validDate);

        blackList.add(refreshToken);
        blackList.add(refreshToken2);

        Thread.sleep(1000);

        // when
        blackList.removeExpiredTokens();

        // then
        assertFalse(blackList.isListed(refreshToken));
        assertTrue(blackList.isListed(refreshToken2));
    }
}