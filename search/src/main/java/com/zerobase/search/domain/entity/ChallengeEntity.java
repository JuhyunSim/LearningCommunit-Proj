package com.zerobase.search.domain.entity;

import com.zerobase.search.domain.dto.ChallengeResponseDto;
import com.zerobase.search.domain.enums.Category;
import com.zerobase.search.domain.enums.ChallengeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "challenge")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeEntity {
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

    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public ChallengeResponseDto.ChallengeSimpleDto toChallengeSimpleDto() {
        return ChallengeResponseDto.ChallengeSimpleDto.builder()
                .id(id)
                .userNickName(userNickName)
                .title(title)
                .status(status)
                .startDate(startDate)
                .dueDate(dueDate)
                .createdAt(createdAt)
                .lastModifiedAt(lastModifiedAt)
                .build();
    }

}
