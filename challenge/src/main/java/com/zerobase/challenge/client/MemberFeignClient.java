package com.zerobase.challenge.client;

import com.zerobase.user.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "challenge-service", url = "http://localhost:8081")
@Component
public interface MemberFeignClient {

    @GetMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<MemberDto> userInfo(
            @RequestHeader("Authorization") String token) throws Exception;

}
