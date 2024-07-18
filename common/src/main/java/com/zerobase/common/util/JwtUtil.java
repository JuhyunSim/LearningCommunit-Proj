package com.zerobase.common.util;

import com.zerobase.common.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.stream.Collectors;

import static io.jsonwebtoken.Jwts.SIG.HS256;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretStr;
    private SecretKey SECRET_KEY;
    private final Long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; //7일
    private final Long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 30; //30분

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretStr);
        SECRET_KEY = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public String generateToken(
            String username, List<GrantedAuthority> authorities
    ) {
        return createToken(username, authorities, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(String username) {
        return createToken(
                username, Collections.emptyList(), REFRESH_TOKEN_EXPIRATION
        ); // 7 days
    }

    private String createToken(String username, List<GrantedAuthority> authorities, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(
                "authorities",
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SECRET_KEY, HS256)
                .compact();
    }


    public Claims extractAllClaims(String token) {
        log.debug("token: {}", token);
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<Role> extractRoles(String token) {
        List<String> roles = extractAllClaims(token).get("authorities", List.class);
        return roles.stream().map(Role::valueOf)
                .collect(Collectors.toList());
    }

    public boolean validateToken(String username, String token) {
        return (extractUsername(token).equals(username) && !isTokenExpired(token)) ;
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public Authentication getAuthentication(String jwt) {
        log.info("Jwt : {}", jwt);
        String username = extractUsername(jwt);
        List<Role> roles = extractRoles(jwt);

        User userDetails = new User(username, "", roles.stream()
                .map(role -> new SimpleGrantedAuthority(String.valueOf(role)))
                .collect(Collectors.toList()));

        return new UsernamePasswordAuthenticationToken(userDetails,
                "", userDetails.getAuthorities());
    }
}
