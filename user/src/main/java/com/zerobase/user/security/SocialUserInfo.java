package com.zerobase.user.security;

import com.zerobase.user.enums.Provider;

public interface SocialUserInfo {
    Provider getProvider();
    String getProviderId();
    String getName();
}
