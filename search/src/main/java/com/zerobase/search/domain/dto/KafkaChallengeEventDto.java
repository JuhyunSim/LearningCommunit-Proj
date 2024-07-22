package com.zerobase.search.domain.dto;

import com.zerobase.kafka.enums.Category;
import com.zerobase.kafka.enums.ChallengeStatus;
import com.zerobase.search.domain.entity.ChallengeEntity;
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

    public ChallengeEntity toEntity() {
        return ChallengeEntity.builder()
                .id(id)
                .userId(userId)
                .userNickName(userNickName)
                .title(title)
                .category(com.zerobase.search.domain.enums.Category.valueOf(category.name()))
                .description(description)
                .startDate(startDate)
                .dueDate(dueDate)
                .description(description)
                .status(com.zerobase.search.domain.enums.ChallengeStatus.valueOf(status.name()))
//                .createdAt(createdAt)
//                .lastModifiedAt(lastModifiedAt)
                .build();
    }
}
