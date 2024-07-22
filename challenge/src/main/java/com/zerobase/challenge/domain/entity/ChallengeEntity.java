package com.zerobase.challenge.domain.entity;

import com.zerobase.challenge.domain.dto.ChallengeResponseDto;
import com.zerobase.challenge.domain.enums.Category;
import com.zerobase.challenge.domain.enums.ChallengeStatus;
import com.zerobase.kafka.dto.KafkaChallengeEventDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import java.time.LocalDate;

@Entity(name = "challenge")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class ChallengeEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userNickName;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false, length = 100)
    private String goal;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    public ChallengeResponseDto toChallengeDto() {
        return ChallengeResponseDto.builder()
                .id(id)
                .userId(userId)
                .userNickName(userNickName)
                .title(title)
                .category(category)
                .goal(goal)
                .startDate(startDate)
                .dueDate(dueDate)
                .description(description)
                .status(status)
                .createdAt(getCreatedAt())
                .lastModifiedAt(getLastModifiedAt())
                .build();
    }

    public ChallengeResponseDto.ChallengeSimpleDto toChallengeSimpleDto() {
        return ChallengeResponseDto.ChallengeSimpleDto.builder()
                .id(id)
                .userNickName(userNickName)
                .title(title)
                .status(status)
                .startDate(startDate)
                .dueDate(dueDate)
                .createdAt(getCreatedAt())
                .lastModifiedAt(getLastModifiedAt())
                .build();
    }

    public KafkaChallengeEventDto toKafkaChallengeEventDto() {
        return KafkaChallengeEventDto.builder()
                .id(id)
                .userId(userId)
                .userNickName(userNickName)
                .title(title)
                .category(com.zerobase.kafka.enums.Category.valueOf(category.name()))
                .goal(goal)
                .startDate(startDate)
                .dueDate(dueDate)
                .description(description)
                .status(com.zerobase.kafka.enums.ChallengeStatus.valueOf(status.name()))
                .createdAt(getCreatedAt())
                .lastModifiedAt(getLastModifiedAt())
                .build();
    }
}
