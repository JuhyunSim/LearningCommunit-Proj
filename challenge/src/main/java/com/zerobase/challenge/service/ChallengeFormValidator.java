package com.zerobase.challenge.service;

import com.zerobase.challenge.domain.dto.ChangeChallengeForm;
import com.zerobase.challenge.domain.dto.CreateChallengeForm;
import com.zerobase.challenge.domain.dto.UpdateChallengeForm;
import com.zerobase.challenge.domain.entity.ChallengeEntity;
import com.zerobase.challenge.domain.repository.ChallengeRepository;
import com.zerobase.challenge.exception.CustomException;
import com.zerobase.challenge.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.zerobase.challenge.domain.enums.ChallengeStatus.ONGOING;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeFormValidator {

    private final ChallengeRepository challengeRepository;

    public void validateCreateChallengeForm(
            Long userId,
            CreateChallengeForm createChallengeForm
    ) {
        //dueDate 기간 안에 이미 등록한 챌린지가 있으면 등록 불가
        //dueDate는 시작날짜로부터 최소 1개월 이후 5년 이내
        validateDuedate(createChallengeForm);
        validateUserForm(userId, createChallengeForm);
    }

    public void validateUpdateChallengeForm(
            ChallengeEntity challengeEntity,
            UpdateChallengeForm updateChallengeForm
    ) {
        // 진행 중인 챌린지는 기존 dueDate를 앞당길 수 없음
        if (challengeEntity.getStatus().equals(ONGOING) &&
                updateChallengeForm.getDueDate()
                        .isBefore(challengeEntity.getDueDate())
        ) {
            throw new CustomException(ErrorCode.SHORTEN_DUEDATE_NOT_AVAILABLE);
        }

        // 이미 진행 중이라면 시작 날짜 변경 불가
        if (challengeEntity.getStatus().equals(ONGOING) &&
                !challengeEntity.getStartDate()
                        .equals(updateChallengeForm.getStartDate())
        ) {
            throw new CustomException(ErrorCode.CHALLENGE_STATUS_NOT_MODIFIABLE);
        }
        //dueDate 기간 안에 이미 등록한 챌린지가 있으면 등록 불가
        //dueDate는 시작날짜로부터 최소 1개월 이후 5년 이내
        validateDuedate(updateChallengeForm);
        validateUserForm(updateChallengeForm.getUserId(), updateChallengeForm);
    }

    public void validateDuedate(ChangeChallengeForm changeChallengeForm) {
        //dueDate 기간 안에 이미 챌린지가 있으면 등록 불가
        boolean existOnGoingChallenge =
                challengeRepository.findAllByUserId(changeChallengeForm.getUserId())
                        .stream()
                        .anyMatch(challenge ->
                                changeChallengeForm.getDueDate().isAfter(challenge.getStartDate()) &&
                                                changeChallengeForm.getDueDate().isBefore(challenge.getDueDate())
                        );
        if (existOnGoingChallenge) {
            throw new CustomException(ErrorCode.INVALID_CHALLENGE_DUEDATE);
        }

        if(changeChallengeForm.getDueDate()
                .isBefore(changeChallengeForm
                        .getStartDate()
                        .plusMonths(1)) ||
                changeChallengeForm.getDueDate()
                        .isAfter(changeChallengeForm
                                .getDueDate()
                                .plusYears(5))
        ) {
            throw new CustomException(ErrorCode.INVALID_CHALLENGE_DUEDATE);
        }
    }

    //user의 아이디와 challenge의 소유자 아이디 일치 여부 확인
    public void validateUserEntity(
            Long userId, ChallengeEntity challengeEntity
    ) {
        if (!Objects.equals(userId, challengeEntity.getUserId())) {
            throw new CustomException(ErrorCode.INVALID_CHALLENGE_USER);
        }
    }
    public void validateUserForm(
            Long userId, ChangeChallengeForm changeChallengeForm) {
        log.debug("userId = {}, challengeForm UserId = {}", userId, changeChallengeForm.getUserId());
        if (!Objects.equals(userId, changeChallengeForm.getUserId())) {
            throw new CustomException(ErrorCode.INVALID_CHALLENGE_USER);
        }
    }
}
