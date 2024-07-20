package com.zerobase.apigateway.client;

import com.zerobase.common.dto.JwtResponse;
import com.zerobase.common.dto.OAuth2UserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "oauth-service", url = "http://localhost:8081")
public interface OAuth2LoginClient {
    @PostMapping("/users/oauth2")
    Mono<JwtResponse> getToken(@RequestBody OAuth2UserDto userDto);
}