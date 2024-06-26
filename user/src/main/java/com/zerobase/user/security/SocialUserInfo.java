package com.zerobase.user.security;

public interface SocialUserInfo {
    String getProvider();
    String getProviderId();
    String getName();
}
