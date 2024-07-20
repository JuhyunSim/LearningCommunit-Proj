package com.zerobase.challenge.domain.entity;

import com.zerobase.challenge.domain.dto.ChallengeResponseDto;
import com.zerobase.challenge.domain.enums.Category;
import com.zerobase.challenge.domain.enums.ChallengeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    private Long userId;

    @Column(nullable = false)
    private String username;

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

    public ChallengeResponseDto toResponseDto() {
        return ChallengeResponseDto.builder()
                .id(id)
                .username(username)
                .title(title)
                .category(category)
                .goal(goal)
                .startDate(startDate)
                .dueDate(dueDate)
                .description(description)
                .status(status)
                .build();
    }

}
