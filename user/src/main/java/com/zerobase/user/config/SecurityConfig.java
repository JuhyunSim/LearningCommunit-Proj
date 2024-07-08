package com.zerobase.user.config;

import com.zerobase.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(withDefaults())
                .cors(withDefaults())
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/login/**",
                                "/register/**",
                                "/csrf-token/**")
                        .permitAll()
                        .requestMatchers("/user").hasRole("USER")
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService))
                                .successHandler((request, response, authentication) -> {
                                    log.info("Login successful: {}", authentication.getPrincipal());
                                    response.sendRedirect("/home");
                                })
                                .failureHandler((request, response, exception) -> {
                                    log.error("Login failed", exception);
                                    response.sendRedirect("/error");
                                }))
                .logout(logout -> logout
                        .logoutSuccessHandler((request, response, authentication) -> {
                            log.info("Logout successful");
                            response.sendRedirect("/login");
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }
}