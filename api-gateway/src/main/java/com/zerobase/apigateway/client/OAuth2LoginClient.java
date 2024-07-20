package com.zerobase.apigateway.client;

import com.zerobase.apigateway.dto.JwtResponse;
import com.zerobase.apigateway.dto.OAuth2UserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "oauth-service", url = "http://localhost:8081")
@Component
public interface OAuth2LoginClient {
    @PostMapping("/users/oauth2")
    Mono<JwtResponse> getToken(@RequestBody OAuth2UserDto userDto);
}