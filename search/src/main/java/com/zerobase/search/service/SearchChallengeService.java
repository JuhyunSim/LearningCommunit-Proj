package com.zerobase.search.service;

import com.zerobase.search.domain.dto.ChallengeResponseDto;
import com.zerobase.search.domain.entity.ChallengeEntity;
import com.zerobase.search.domain.enums.ChallengeStatus;
import com.zerobase.search.domain.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchChallengeService {

    private final ChallengeRepository challengeRepository;

    // 챌린지 검색
    public List<ChallengeResponseDto.ChallengeSimpleDto> searchChallenges(
            String title, String userNickName, ChallengeStatus status, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // 빈 검색어 처리
        if (title == null && userNickName == null && status == null) {
            return challengeRepository.findAll(pageable).getContent().stream()
                    .map(ChallengeEntity::toChallengeSimpleDto).toList();
        }

        // 기본 검색 쿼리 실행
        List<ChallengeEntity> challenges =
                challengeRepository.searchChallenges(
                        title, userNickName, status
                );
        log.info("Challenges found: {}", challenges.size());
        challenges.forEach(challenge -> log.info("Challenge: {}", challenge));

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), challenges.size());
        return challenges.subList(start, end).stream()
                .map(ChallengeEntity::toChallengeSimpleDto).toList();
    }
}
