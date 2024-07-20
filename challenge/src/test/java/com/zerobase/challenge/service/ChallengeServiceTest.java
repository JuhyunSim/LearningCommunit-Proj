package com.zerobase.challenge.service;

import com.zerobase.challenge.client.MemberFeignClient;
import com.zerobase.challenge.domain.dto.ChallengeResponseDto;
import com.zerobase.challenge.domain.dto.CreateChallengeForm;
import com.zerobase.challenge.domain.dto.UpdateChallengeForm;
import com.zerobase.challenge.domain.entity.ChallengeEntity;
import com.zerobase.challenge.domain.repository.ChallengeRepository;
import com.zerobase.challenge.exception.CustomException;
import com.zerobase.challenge.exception.ErrorCode;
import com.zerobase.challenge.domain.dto.MemberDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.zerobase.challenge.domain.enums.Category.LANGUAGE;
import static com.zerobase.challenge.domain.enums.ChallengeStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @InjectMocks
    private ChallengeService challengeService;

    @Mock
    private MemberFeignClient memberFeignClient;

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private ChallengeFormValidator challengeFormValidator;

    private String token;
    private MemberDto memberDto;
    private ChallengeEntity challengeEntity;


    @Test
    public void createChallenge_Success() throws Exception {
        //given
        token = "Bearer testToken";
        memberDto = MemberDto.builder()
                .id(1L)
                .build();

        CreateChallengeForm createChallengeForm = CreateChallengeForm.builder()
                .title("Test Challenge")
                .username("testUser")
                .goal("Goal Test")
                .startDate(LocalDate.of(2024, 8, 1))
                .dueDate(LocalDate.of(2025, 8, 1))
                .description("test description")
                .build();

        ChallengeEntity challengeEntity = createChallengeForm.toEntity();
        challengeEntity.setStatus(
                createChallengeForm.getStartDate().equals(new Date()) ?
                ONGOING : PENDING
        );

        when(memberFeignClient.userInfo(token)).thenReturn(ResponseEntity.ok(memberDto));
        when(challengeRepository.save(any(ChallengeEntity.class))).thenReturn(challengeEntity);

        //when
        ChallengeResponseDto result = challengeService.createChallenge(token, createChallengeForm);

        assertNotNull(result);
        assertEquals("Test Challenge", result.getTitle());
        assertEquals("testUser", result.getUsername());
        assertEquals("Goal Test", result.getGoal());
        assertEquals(LocalDate.of(2024, 8, 1), result.getStartDate());
        assertEquals(LocalDate.of(2025, 8, 1), result.getDueDate());
        assertEquals(PENDING, result.getStatus());

        verify(challengeRepository, times(1)).save(any(ChallengeEntity.class));
        verify(challengeFormValidator, times(1)).validateCreateChallengeForm(anyLong(), any());
    }


    //dueDate는 시작날짜로부터 최소 1개월 이후 5년 이내
    @Test
    void createChallenge_fail_dueDate_tooShort() throws Exception {
        //given

        token = "Bearer testToken";
        memberDto = MemberDto.builder()
                .id(1L)
                .build();
        CreateChallengeForm createChallengeForm = CreateChallengeForm.builder()
                .startDate(LocalDate.of(2024, 8, 1))
                .dueDate(LocalDate.of(2025, 8, 31))
                .build();
        challengeEntity = createChallengeForm.toEntity();

        when(memberFeignClient.userInfo(token)).thenReturn(ResponseEntity.ok(memberDto));
        doThrow(new CustomException(ErrorCode.INVALID_CHALLENGE_DUEDATE))
                .when(challengeFormValidator)
                .validateCreateChallengeForm(
                        anyLong(), any(CreateChallengeForm.class));
        //when
        CustomException exception =
                assertThrows(CustomException.class,
                        () -> challengeService
                                .createChallenge(token, createChallengeForm));
        //then
        assertNotNull(exception);
        assertEquals("챌린지 진행 기간을 확인하세요", exception.getMessage());
    }

    @Test
    public void updateChallenge_Success() throws Exception {
        //given
        token = "Bearer testToken";
        memberDto = MemberDto.builder()
                .id(1L)
                .build();

        challengeEntity = ChallengeEntity.builder()
                .id(1L)
                .userId(1L)
                .title("Test Challenge")
                .category(LANGUAGE)
                .goal("Run 5km")
                .startDate(LocalDate.now())
                .dueDate(LocalDate.now().plusMonths(1))
                .description("Test Description")
                .status(PENDING)
                .build();

        UpdateChallengeForm updateChallengeForm = UpdateChallengeForm.builder()
                .challengeId(1L)
                .title("Updated Challenge")
                .category(LANGUAGE)
                .startDate(LocalDate.of(2024, 8, 2))
                .dueDate(LocalDate.of(2025, 8, 2))
                .description("updated description")
                .build();

        ChallengeEntity updatedChallengeEntity =
                updateChallengeForm.toUpdatedChallengeEntity(
                        challengeEntity.getId(),
                        challengeEntity.getUserId(),
                        challengeEntity.getUsername(),
                        challengeEntity.getStatus()
                );

        when(challengeRepository.findUpdatableById(1L)).thenReturn(Optional.of(challengeEntity));
        when(memberFeignClient.userInfo(token)).thenReturn(ResponseEntity.ok(memberDto));
        when(challengeRepository.save(any(ChallengeEntity.class))).thenReturn(updatedChallengeEntity);

        //when
        ChallengeResponseDto result = challengeService.updateChallenge(token, updateChallengeForm);

        assertNotNull(result);
        assertEquals("Updated Challenge", result.getTitle());
        assertEquals("updated description", result.getDescription());
        assertEquals(LANGUAGE, result.getCategory());
        assertEquals(LocalDate.of(2024, 8, 2), result.getStartDate());
        assertEquals(LocalDate.of(2025, 8, 2), result.getDueDate());
        assertEquals(PENDING, result.getStatus());
        verify(challengeRepository, times(1)).save(any(ChallengeEntity.class));
        verify(challengeFormValidator, times(1)).validateUpdateChallengeForm(any(), any());
    }

    @Test
    public void cancelStatus_Success() throws Exception {
        //given
        token = "Bearer testToken";
        memberDto = MemberDto.builder()
                .id(1L)
                .build();
        challengeEntity = ChallengeEntity.builder()
                .id(1L)
                .status(PENDING)
                .build();



        when(challengeRepository.findUpdatableById(1L)).thenReturn(Optional.of(challengeEntity));
        when(memberFeignClient.userInfo(token)).thenReturn(ResponseEntity.ok(memberDto));
        challengeEntity.setStatus(CANCELLED);
        when(challengeRepository.save(any(ChallengeEntity.class))).thenReturn(challengeEntity);

        //when
        ChallengeResponseDto result = challengeService.cancelStatus(token, 1L);

        //then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(CANCELLED, result.getStatus());
        verify(challengeRepository, times(1)).save(any(ChallengeEntity.class));
        verify(challengeFormValidator, times(1)).validateUserEntity(anyLong(), any());
    }

    @Test
    public void updateExpiredChallenges_Success() {
        //given
        challengeEntity = ChallengeEntity.builder()
                .dueDate(LocalDate.now().minusDays(1))
                .status(ONGOING).build();

        when(challengeRepository.findByStatus(ONGOING)).thenReturn(List.of(challengeEntity));
        when(challengeRepository.save(any(ChallengeEntity.class))).thenReturn(challengeEntity);

        //when
        challengeService.updateExpiredChallenges();

        //then
        verify(challengeRepository, times(1)).save(any(ChallengeEntity.class));
        assertEquals(EXPIRED, challengeEntity.getStatus());
    }
}