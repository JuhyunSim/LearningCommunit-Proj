package com.zerobase.user.service;

import com.zerobase.user.entity.MemberEntity;
import com.zerobase.user.enums.MemberLevel;
import com.zerobase.user.enums.Provider;
import com.zerobase.user.repository.MemberRepository;
import com.zerobase.user.security.SocialUserInfo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final HttpSession httpSession;
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //로그 추가: OAuth2UserRequest 정보
        log.info("OAuth2UserRequest: {}", userRequest);

        //로그인 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2User: {}", oAuth2User);

        //어떤 소셜 계정(provider)을 사용했는지 가져오기
        String providerStr =
                userRequest.getClientRegistration()
                        .getRegistrationId().toUpperCase(Locale.ROOT);
        Provider provider = Provider.valueOf(providerStr);
        log.info("Provider: {}", providerStr);

        //provider에 따라 필요한 정보 mapping
        SocialUserInfo userInfo = provider.createUserInfo(oAuth2User.getAttributes());
        log.info("UserInfo: {}", userInfo);

        //사용자의 식별값(provider마다 다름)과 사용자명 가져오기
        String providerId = userInfo.getProviderId();
        String name = userInfo.getName();
        log.info("ProviderId: {}, Name: {}", providerId, name);

        // 사용자 정보 저장 또는 업데이트
        Optional<MemberEntity> memberOptional =
                memberRepository.findByProviderAndProviderId(provider, providerId);
        if (memberOptional.isEmpty()) {
            MemberEntity memberEntity = MemberEntity.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .name(name)
                    .level(MemberLevel.BEGINNER)
                    .points(0L)
                    .build();

            memberRepository.save(memberEntity);
            log.info("New MemberEntity saved: name {}",
                    memberEntity.getName());
        } else {
            log.info("Existing MemberEntity found: name {}",
                    memberOptional.get().getName());
        }


        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                provider.getUserNameAttribute()  //provider에 따라 다른 식별값 사용
        );
    }
}
