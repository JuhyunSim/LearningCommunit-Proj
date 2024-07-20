package com.zerobase.apigateway.oauth2User;

import com.zerobase.apigateway.enums.Provider;

public interface SocialUserInfo {
    Provider getProvider();
    String getProviderId();
    String getName();
}
