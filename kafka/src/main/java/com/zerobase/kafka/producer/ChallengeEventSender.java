package com.zerobase.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.kafka.dto.KafkaChallengeEventDto;
import com.zerobase.kafka.enums.KafkaTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChallengeEventSender {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendChallengeEvent(
            KafkaTopic kafkaTopic, KafkaChallengeEventDto kafkaChallengeEventDto
    ) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(kafkaChallengeEventDto);
        kafkaTemplate.send(kafkaTopic.getName(), message);
    }
}
