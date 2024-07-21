package com.zerobase.challenge.controller;

import com.zerobase.challenge.domain.dto.CreateChallengeForm;
import com.zerobase.challenge.domain.dto.UpdateChallengeForm;
import com.zerobase.challenge.service.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenges")
@Slf4j
public class ChallengeController {

    private final ChallengeService challengeService;
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> create(
            @RequestHeader(name = "Authorization") String token,
            @Valid @RequestBody CreateChallengeForm createChallengeForm
    ) throws Exception {
        return ResponseEntity.ok(
                challengeService.createChallenge(token, createChallengeForm)
        );
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> update(
            @RequestHeader(name = "Authorization") String token,
            @Valid @RequestBody UpdateChallengeForm updateChallengeForm
    ) throws Exception {
        return ResponseEntity.ok(challengeService.updateChallenge(token, updateChallengeForm));
    }

    @PutMapping("/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelStatus(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam Long challengeId
    ) throws Exception {
        return ResponseEntity.ok(
                challengeService.cancelStatus(token, challengeId)
        );
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getChallenges(
            @RequestHeader(name = "Authorization") String token
    ) throws Exception {
        return ResponseEntity.ok(challengeService.getChallenges(token));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getChallenge(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam Long challengeId
    ) throws Exception {
        return ResponseEntity.ok(challengeService.getChallenge(token, challengeId));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteChallenge(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam Long challengeId
    ) throws Exception {
        challengeService.deleteChallenge(token, challengeId);
        log.info("{} 챌린지를 삭제하였습니다.", challengeId);
        return ResponseEntity.ok().build();
    }

}
