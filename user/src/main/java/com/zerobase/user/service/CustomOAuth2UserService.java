package com.zerobase.user.service;

import com.zerobase.user.entity.MemberEntity;
import com.zerobase.user.enums.MemberLevel;
import com.zerobase.user.repository.MemberRepository;
import com.zerobase.user.security.GoogleUserInfo;
import com.zerobase.user.security.KakaoUserInfo;
import com.zerobase.user.security.NaverUserInfo;
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
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final HttpSession httpSession;
    private final MemberRepository memberRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        SocialUserInfo userInfo = getUserInfo(provider, oAuth2User.getAttributes());

        String providerId = userInfo.getProviderId();
        String name = userInfo.getName();

        // 사용자 정보 저장 또는 업데이트
        Optional<MemberEntity> userOptional = memberRepository.findByProviderAndProviderId(provider, providerId);
        if (userOptional.isEmpty()) {
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setProvider(provider);
            memberEntity.setProviderId(providerId);
            memberEntity.setName(name);
            memberEntity.setLevel(MemberLevel.BEGINNER);
            memberEntity.setPoints(0L);
            memberRepository.save(memberEntity);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                getUsernameAttributeName(provider)  //provider에 따라 다른 식별값 사용
        );
    }

    private SocialUserInfo getUserInfo(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return new GoogleUserInfo(attributes);
            case "naver":
                return new NaverUserInfo(attributes);
            case "kakao":
                return new KakaoUserInfo(attributes);
            default:
                throw new OAuth2AuthenticationException("Unknown provider: " + provider);
        }
    }

    private String getUsernameAttributeName(String provider) {
        if ("google".equals(provider)) {
            return "sub";
        } else if ("naver".equals(provider)) {
            return "response";
        } else if ("kakao".equals(provider)) {
            return "id";
        }
        return "id";
    }
}
