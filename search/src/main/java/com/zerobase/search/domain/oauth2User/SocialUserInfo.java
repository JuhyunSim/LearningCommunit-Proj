package com.zerobase.search.domain.oauth2User;


import com.zerobase.search.domain.enums.Provider;

public interface SocialUserInfo {
    Provider getProvider();
    String getProviderId();
    String getName();
}
