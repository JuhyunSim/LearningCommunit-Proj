package com.zerobase.challenge.service;

import com.zerobase.challenge.domain.dto.CreateChallengeForm;
import com.zerobase.challenge.domain.dto.UpdateChallengeForm;
import com.zerobase.challenge.domain.entity.ChallengeEntity;
import com.zerobase.challenge.domain.enums.Category;
import com.zerobase.challenge.domain.repository.ChallengeRepository;
import com.zerobase.challenge.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static com.zerobase.challenge.domain.enums.ChallengeStatus.ONGOING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChallengeFormValidatorTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeFormValidator challengeFormValidator;

    private CreateChallengeForm createChallengeForm;
    private UpdateChallengeForm updateChallengeForm;
    private ChallengeEntity challengeEntity;

    @BeforeEach
    void setUp() {
        createChallengeForm = CreateChallengeForm.builder()
                .userId(1L)
                .title("Title")
                .category(Category.LANGUAGE)
                .goal("master")
                .startDate(LocalDate.of(2025, 1, 1))
                .dueDate(LocalDate.of(2026, 1, 1))
                .description("Description")
                .build();

        updateChallengeForm = UpdateChallengeForm.builder()
                .challengeId(1L)
                .userId(1L)
                .title("Update Title")
                .category(Category.LANGUAGE)
                .goal("update")
                .startDate(LocalDate.of(2025, 1, 1))
                .dueDate(LocalDate.of(2025, 3, 1))
                .description("Update Description")
                .build();

        challengeEntity = ChallengeEntity.builder()
                .id(1L)
                .userId(1L)
                .title("Existing Challenge")
                .category(Category.LANGUAGE)
                .goal("master")
                .startDate(LocalDate.now().minusDays(10))
                .dueDate(LocalDate.now().plusMonths(1).plusDays(10))
                .description("Existing Description")
                .status(ONGOING)
                .build();
    }

    //챌린지 기간이 1개월 미만일 때
    @Test
    void validateCreateChallengeForm_dueDateIsBeforeStartDate() {
        //given
        createChallengeForm = CreateChallengeForm.builder()
                .userId(1L)
                .title("Title")
                .category(Category.LANGUAGE)
                .goal("master")
                .startDate(LocalDate.of(2025, 1, 1))
                .dueDate(LocalDate.of(2025, 1, 8))
                .description("Description")
                .build();

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            challengeFormValidator.validateCreateChallengeForm(1L, createChallengeForm);
        });
        //then
        assertEquals("챌린지 진행 기간을 확인하세요", exception.getMessage());
    }

    //아직 진행 중인 챌린지가 있을 때
    @Test
    void validateCreateChallengeForm_OngoingChallengeAlreadyExists() {
        //given
        challengeEntity = ChallengeEntity.builder()
                .id(1L)
                .userId(1L)
                .status(ONGOING)
                .build();

        when(challengeRepository.findAllByUserId(1L)).thenReturn(List.of(challengeEntity));
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            challengeFormValidator.validateCreateChallengeForm(1L, createChallengeForm);
        });
        //then
        assertEquals("챌린지 진행 기간을 확인하세요", exception.getMessage());
    }

    //진행 중인 챌린지의 마감 기한을 앞당겼을 때
    @Test
    void validateUpdateChallengeForm_whenDueDateIsShortened() {
        //given
        challengeEntity = ChallengeEntity.builder()
                .id(1L)
                .userId(1L)
                .title("Existing Challenge")
                .category(Category.LANGUAGE)
                .goal("master")
                .startDate(LocalDate.of(2025, 1, 1))
                .dueDate(LocalDate.of(2025, 3, 1))
                .description("Existing Description")
                .status(ONGOING)
                .build();
        updateChallengeForm = updateChallengeForm.builder()
                .dueDate(LocalDate.of(2025, 2, 2))
                .build();
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            challengeFormValidator.validateUpdateChallengeForm(challengeEntity, updateChallengeForm);
        });
        //then
        assertEquals("이미 진행 중인 챌린지는 기한을 앞당길 수 없습니다.", exception.getMessage());
    }

    // 이미 진행 중이라면 시작 날짜 변경 불가
    @Test
    void validateUpdateChallengeForm_shouldThrowException_whenStartDateIsChanged() {
        challengeEntity = ChallengeEntity.builder()
                .id(1L)
                .userId(1L)
                .title("Existing Challenge")
                .category(Category.LANGUAGE)
                .goal("master")
                .startDate(LocalDate.of(2025, 1, 1))
                .dueDate(LocalDate.of(2025, 3, 1))
                .description("Existing Description")
                .status(ONGOING)
                .build();
        updateChallengeForm = updateChallengeForm.builder()
                .startDate(LocalDate.of(2024, 12, 31))
                .dueDate(LocalDate.of(2025, 3, 1))
                .build();
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            challengeFormValidator.validateUpdateChallengeForm(challengeEntity, updateChallengeForm);
        });
        //then
        assertEquals("이미 진행 중이거나 완료 또는 만료 상태인 챌린지는 변경할 수 없습니다.", exception.getMessage());
    }

    @Test
    void validateUserEntity_UserIdNotMatch() {
        challengeEntity = ChallengeEntity.builder()
                .id(1L)
                .userId(2L)
                .title("Existing Challenge")
                .category(Category.LANGUAGE)
                .goal("master")
                .startDate(LocalDate.of(2025, 1, 1))
                .dueDate(LocalDate.of(2025, 3, 1))
                .description("Existing Description")
                .status(ONGOING)
                .build();
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            challengeFormValidator.validateUserEntity(1L, challengeEntity);
        });
        //then
        assertEquals("챌린지의 유저 아이디가 토큰 소유자의 아이디와 일치하지 않습니다.",
                exception.getMessage());
    }

    @Test
    void validateUserForm_shouldThrowException_whenUserIdDoesNotMatch() {
        //then
        updateChallengeForm = updateChallengeForm.builder()
                .userId(2L)
                .build();
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            challengeFormValidator.validateUserForm(1L, updateChallengeForm);
        });
        //then
        assertEquals("챌린지의 유저 아이디가 토큰 소유자의 아이디와 일치하지 않습니다.", exception.getMessage());
    }
}