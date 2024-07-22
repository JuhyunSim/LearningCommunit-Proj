package com.zerobase.search.client;

import com.zerobase.search.domain.dto.ChallengeResponseDto;
import com.zerobase.search.domain.enums.ChallengeStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "search-service", url = "localhost:8082/challenges")
@Component
public interface ChallengeFeignClient {

    @GetMapping("/challenges/search")
    ResponseEntity<List<ChallengeResponseDto.ChallengeSimpleDto>> searchChallenges(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String userNickName,
            @RequestParam(required = false) ChallengeStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    );
}
