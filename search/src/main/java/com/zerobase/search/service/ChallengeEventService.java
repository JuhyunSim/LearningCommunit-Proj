package com.zerobase.search.service;

import com.zerobase.kafka.dto.KafkaChallengeEventDto;
import com.zerobase.search.domain.entity.ChallengeEntity;
import com.zerobase.search.domain.enums.Category;
import com.zerobase.search.domain.enums.ChallengeStatus;
import com.zerobase.search.domain.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeEventService {
    private final ChallengeRepository challengeRepository;

    public void handleChallengeCreated(KafkaChallengeEventDto event) {
        log.info("Handling challenge created event: {}", event.getTitle());
        challengeRepository.save(eventToEntity(event));
    }

    public void handleChallengeUpdated(KafkaChallengeEventDto event) {
        challengeRepository.save(eventToEntity(event));
    }

    public void handleChallengeDeleted(KafkaChallengeEventDto event) {
        challengeRepository.deleteById(event.getId());
    }

    private ChallengeEntity eventToEntity(KafkaChallengeEventDto event) {
        return ChallengeEntity.builder()
                        .id(event.getId())
                        .userId(event.getUserId())
                        .userNickName(event.getUserNickName())
                        .title(event.getTitle())
                        .category(Category.valueOf(event.getCategory().name()))
                        .goal(event.getGoal())
                        .startDate(event.getStartDate())
                        .dueDate(event.getDueDate())
                        .description(event.getDescription())
                        .status(ChallengeStatus.valueOf(event.getStatus().name()))
                        .createdAt(event.getCreatedAt())
                        .lastModifiedAt(event.getLastModifiedAt())
                        .build();
    }
}
