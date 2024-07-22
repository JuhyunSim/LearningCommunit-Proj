package com.zerobase.search.domain.enums;


import com.zerobase.search.domain.oauth2User.GoogleUserInfo;
import com.zerobase.search.domain.oauth2User.KakaoUserInfo;
import com.zerobase.search.domain.oauth2User.NaverUserInfo;
import com.zerobase.search.domain.oauth2User.SocialUserInfo;
import lombok.Getter;

import java.util.Map;

@Getter
public enum Provider {
    GOOGLE("sub"){
        @Override
        public GoogleUserInfo createUserInfo(Map<String, Object> attributes) {
            return new GoogleUserInfo(attributes);
        }
    },
    NAVER("response"){
        @Override
        public NaverUserInfo createUserInfo(Map<String, Object> attributes) {
            return new NaverUserInfo(attributes);
        }
    },
    KAKAO("id"){
        @Override
        public KakaoUserInfo createUserInfo(Map<String, Object> attributes) {
            return new KakaoUserInfo(attributes);
        }
    };

    private final String userNameAttribute;

    Provider(String userNameAttribute) {
        this.userNameAttribute = userNameAttribute;
    }

    public abstract SocialUserInfo createUserInfo(Map<String, Object> attributes);
}
