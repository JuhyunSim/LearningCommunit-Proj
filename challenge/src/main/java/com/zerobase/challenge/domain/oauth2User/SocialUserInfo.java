package com.zerobase.challenge.domain.oauth2User;


import com.zerobase.challenge.domain.enums.Provider;

public interface SocialUserInfo {
    Provider getProvider();
    String getProviderId();
    String getName();
}
