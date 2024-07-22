package com.zerobase.search.controller;

import com.zerobase.search.domain.enums.ChallengeStatus;
import com.zerobase.search.service.SearchChallengeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class SearchChallengeController {

    private final SearchChallengeService searchChallengeService;

    @GetMapping("/challenges")
    public ResponseEntity<?> searchChallenges(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) ChallengeStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                searchChallengeService.searchChallenges(
                        title, nickname, status, page, size)
        );
    }
}
