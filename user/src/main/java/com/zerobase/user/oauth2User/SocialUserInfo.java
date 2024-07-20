package com.zerobase.user.oauth2User;

import com.zerobase.user.enums.Provider;

public interface SocialUserInfo {
    Provider getProvider();
    String getProviderId();
    String getName();
}
