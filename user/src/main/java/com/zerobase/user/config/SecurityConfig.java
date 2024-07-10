package com.zerobase.user.config;

import com.zerobase.user.service.BlackList;
import com.zerobase.user.service.CustomOAuth2UserService;
import com.zerobase.user.service.LogoutService;
import com.zerobase.user.service.SecurityMemberService;
import com.zerobase.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final SecurityMemberService securityMemberService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final LogoutService logoutService;
    private final BlackList blackList;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, blackList);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/",
                                "users/login/**",
                                "/users/register/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/csrf-token")
                        .permitAll()
                        .requestMatchers("/users/**", "/users/refresh/**").hasRole("USER")
                        .requestMatchers("/logout").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService))
                                .successHandler((request, response, authentication) -> {
                                    log.info("Login successful: {}", authentication.getPrincipal());
                                    List<GrantedAuthority> authorities =
                                            new ArrayList<>(authentication.getAuthorities());
                                    log.debug("authorities: {}", authorities);
                                    String accessJwt =
                                            jwtUtil.generateToken(authentication.getName(), authorities);
                                    String refreshToken =
                                            jwtUtil.generateRefreshToken(authentication.getName());

                                    log.debug("token: {}", accessJwt);
                                    log.debug("refreshToken: {}", refreshToken);
                                    response.setContentType("application/json");
                                    response.setCharacterEncoding("UTF-8");
                                    response.getWriter().write(
                                            "{\"accessToken\": \""
                                                    + accessJwt
                                                    + "\", \"refreshToken\": \""
                                                    + refreshToken + "\"}");
                                })
                                .failureHandler((request, response, exception) -> {
                                    log.error("Login failed", exception);
                                    response.sendRedirect("/error");
                                }))
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return securityMemberService;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(securityMemberService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
}