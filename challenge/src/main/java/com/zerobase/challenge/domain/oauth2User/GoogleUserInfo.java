package com.zerobase.challenge.domain.oauth2User;


import com.zerobase.challenge.domain.enums.Provider;

import java.util.Map;

public class GoogleUserInfo implements SocialUserInfo {
    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
