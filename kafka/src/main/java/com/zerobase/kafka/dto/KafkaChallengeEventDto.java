package com.zerobase.kafka.dto;

import com.zerobase.kafka.enums.Category;
import com.zerobase.kafka.enums.ChallengeStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaChallengeEventDto {
    private Long id;
    private Long userId;
    private String userNickName;
    private String title;
    private Category category;
    private String goal;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String description;
    private ChallengeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;


}
