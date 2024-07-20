package com.zerobase.user.oauth2User;

import com.zerobase.user.enums.Provider;

import java.util.Map;

public class KakaoUserInfo implements SocialUserInfo {
    private final Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }


    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");
    }
}
