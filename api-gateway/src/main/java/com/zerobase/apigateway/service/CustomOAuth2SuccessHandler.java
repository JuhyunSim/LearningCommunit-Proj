package com.zerobase.apigateway.service;

import com.zerobase.apigateway.client.OAuth2LoginClient;
import com.zerobase.common.dto.OAuth2UserDto;
import com.zerobase.common.enums.Provider;
import com.zerobase.common.enums.Role;
import com.zerobase.common.oauth2User.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler implements ServerAuthenticationSuccessHandler {
    private final OAuth2LoginClient oAuth2LoginClient;
    private static final String PRE_FIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_TOKEN = "Refresh";

    @Override
    public Mono<Void> onAuthenticationSuccess(
            WebFilterExchange webFilterExchange, Authentication authentication
    ) {
        log.debug("authentication: {}", authentication);
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        log.debug("oAuth2User: {}", oAuth2User);
        log.debug("oAuth2User.getAttributes(): {}", oAuth2User.getAttributes());
        Provider userProvider = Provider.valueOf(oauthToken
                        .getAuthorizedClientRegistrationId()
                        .toUpperCase());

        SocialUserInfo userInfo = userProvider.createUserInfo(oAuth2User.getAttributes());
        String userProviderId = userInfo.getProviderId();
        String name = userInfo.getName();
        OAuth2UserDto oAuth2UserDto = OAuth2UserDto.builder()
                .provider(userProvider)
                .providerId(userProviderId)
                .roles(List.of(Role.ROLE_USER))
                .name(name)
                .attributes(oAuth2User.getAttributes())
                .build();


        return oAuth2LoginClient.getToken(oAuth2UserDto)
                .flatMap(jwtResponse -> {
                    log.info("Access JWT: {}", jwtResponse.getAccessJwt());
                    log.info("Refresh JWT: {}", jwtResponse.getRefreshJwt());
                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    response.getHeaders().add(AUTHORIZATION_HEADER, PRE_FIX + jwtResponse.getAccessJwt());
                    response.getHeaders().add(REFRESH_TOKEN, PRE_FIX + jwtResponse.getRefreshJwt());
                    return webFilterExchange.getExchange().getSession().doOnNext(webSession -> {
                        webSession.getAttributes().put("accessJwt", jwtResponse.getAccessJwt());
                        webSession.getAttributes().put("refreshJwt", jwtResponse.getRefreshJwt());
                    }).then();
                });
    }
}