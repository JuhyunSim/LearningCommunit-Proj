package com.zerobase.common.oauth2User;

import com.zerobase.common.enums.Provider;

public interface SocialUserInfo {
    Provider getProvider();
    String getProviderId();
    String getName();
}
