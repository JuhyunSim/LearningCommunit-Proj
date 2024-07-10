package com.zerobase.user.util;

import com.zerobase.user.enums.Role;
import com.zerobase.user.service.SecurityAdminService;
import com.zerobase.user.service.SecurityMemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretStr;
    private SecretKey SECRET_KEY;
    private final SecurityAdminService securityAdminService;
    private final SecurityMemberService securityMemberService;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretStr);
        SECRET_KEY = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public String generateToken(String username, List<GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(
                "authorities",
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SECRET_KEY)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }


    public List<Role> extractRoles(String token) {
        List<String> roles = extractAllClaims(token).get("authorities", List.class);
        return roles.stream().map(s -> Role.valueOf(s.substring("ROLE_".length())))
                .collect(Collectors.toList());
    }

    public boolean validateToken(String token, String username) {
        return (extractUsername(token).equals(username) && !isTokenExpired(token));
    }

    public Authentication getAuthentication(String jwt) {
        log.info("Jwt : {}", jwt);

        List<Role> roles = extractRoles(jwt);

        UserDetails userDetails = getUserDetails(jwt, roles);

        return new UsernamePasswordAuthenticationToken(userDetails,
                "", userDetails.getAuthorities());
    }

    private UserDetails getUserDetails(String jwt, List<Role> roles) {
        String username = this.extractUsername(jwt).trim();
        if (roles.contains(Role.ADMIN)) {
            return this.securityAdminService.loadUserByUsername(username);
        } else if (roles.contains(Role.USER)) {
            return this.securityMemberService.loadUserByUsername(username);
        }
        return null;
    }
}
