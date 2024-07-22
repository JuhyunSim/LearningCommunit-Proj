package com.zerobase.search.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.kafka.dto.KafkaChallengeEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeEventConsumer {

    private final ChallengeEventService challengeEventService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "challenge-created", groupId = "search-service-group")
    public void consumeChallengeCreated(String message) {
        try {
            KafkaChallengeEventDto event = objectMapper.readValue(message, KafkaChallengeEventDto.class);
            log.info("Received Challenge Created: {}", event);
            challengeEventService.handleChallengeCreated(event);
        } catch (JsonProcessingException e) {
            log.error("Error processing challenge-created event", e);
        }
    }

    @KafkaListener(topics = "challenge-updated", groupId = "search-service-group")
    public void consumeChallengeUpdated(String message) {
        try {
            KafkaChallengeEventDto event = objectMapper.readValue(message, KafkaChallengeEventDto.class);
            log.info("Received Challenge Updated: {}", event);
            challengeEventService.handleChallengeUpdated(event);
        } catch (JsonProcessingException e) {
            log.error("Error processing challenge-updated event", e);
        }
    }

    @KafkaListener(topics = "challenge-deleted", groupId = "search-service-group")
    public void consumeChallengeDeleted(String message) {
        try {
            KafkaChallengeEventDto event = objectMapper.readValue(message, KafkaChallengeEventDto.class);
            log.info("Received Challenge Deleted: {}", event);
            challengeEventService.handleChallengeDeleted(event);
        } catch (JsonProcessingException e) {
            log.error("Error processing challenge-deleted event", e);
        }
    }
}