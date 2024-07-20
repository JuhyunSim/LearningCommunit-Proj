package com.zerobase.challenge.service;

import com.zerobase.challenge.client.MemberFeignClient;
import com.zerobase.challenge.domain.dto.ChallengeResponseDto;
import com.zerobase.challenge.domain.dto.CreateChallengeForm;
import com.zerobase.challenge.domain.dto.UpdateChallengeForm;
import com.zerobase.challenge.domain.entity.ChallengeEntity;
import com.zerobase.challenge.domain.enums.ChallengeStatus;
import com.zerobase.challenge.domain.repository.ChallengeRepository;
import com.zerobase.challenge.exception.CustomException;
import com.zerobase.challenge.exception.ErrorCode;
import com.zerobase.challenge.domain.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.zerobase.challenge.domain.enums.ChallengeStatus.*;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final MemberFeignClient memberFeignClient;
    private final ChallengeRepository challengeRepository;
    private final ChallengeFormValidator challengeFormValidator;

    //챌린지 생성
    public ChallengeResponseDto createChallenge(
            String token,
            CreateChallengeForm createChallengeForm
    ) throws Exception {

        Long userId = getUserId(token);
        challengeFormValidator.validateCreateChallengeForm(
                userId, createChallengeForm
        );
        //challengeEntity 생성 후 저장
        //startDate가 오늘 날짜면 상태를 진행 중, 그렇지 않으면 대기 중으로 설정
        ChallengeEntity challengeEntity = createChallengeForm.toEntity();
        challengeEntity.setStatus(
                createChallengeForm.getStartDate().equals(new Date()) ?
                        ONGOING : PENDING
        );
        return challengeRepository.save(challengeEntity).toResponseDto();
    }

    @Scheduled(cron = "0 0 1 * * ?") // 매일 밤 1시에 실행
    public void updateExpiredChallenges() {
        List<ChallengeEntity> ongoingChallenges = challengeRepository.findByStatus(ONGOING);
        ongoingChallenges.stream()
                .filter(challenge -> challenge.getDueDate().isBefore(LocalDate.now()))
                .forEach(challenge -> {
                    challenge.setStatus(ChallengeStatus.EXPIRED);
                    challengeRepository.save(challenge);
                });
    }

    //챌린지 내용 수정
    public ChallengeResponseDto updateChallenge(
            String token,
            UpdateChallengeForm updateChallengeForm
    ) throws Exception {
        ChallengeEntity challengeEntity =
                challengeRepository.findUpdatableById(updateChallengeForm.getChallengeId())
                        .orElseThrow(
                                () -> new CustomException(ErrorCode.UPDATABLE_CHALLENGE_NOT_FOUNT)
                        );

        //token 소유자의 아이디와 챌린지의 유저아이디 일치 확인
        Long userId = getUserId(token);
        challengeFormValidator.validateUserEntity(userId, challengeEntity);
        //duedate & status 검증
        challengeFormValidator.validateUpdateChallengeForm(
                challengeEntity, updateChallengeForm
        );

        //엔티티 수정 (chaellengeId, userId, username은 기존 것으로 사용, 변경 불가)
        ChallengeEntity updatedChallengeEntity =
                updateChallengeForm.toUpdatedChallengeEntity(
                        challengeEntity.getId(),
                        challengeEntity.getUserId(),
                        challengeEntity.getUsername(),
                        challengeEntity.getStatus()
                );

        ChallengeEntity updatedChallenge =
                challengeRepository.save(updatedChallengeEntity);
        return updatedChallenge.toResponseDto();
    }

    //챌린지 취소
    public ChallengeResponseDto cancelStatus(
            String token, Long challengeId
            ) throws Exception {
        ChallengeEntity challengeEntity = challengeRepository
                .findUpdatableById(challengeId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.UPDATABLE_CHALLENGE_NOT_FOUNT)
                );

        //token 소유자의 아이디와 챌린지의 유저아이디 일치 확인
        Long userId = getUserId(token);
        challengeFormValidator.validateUserEntity(userId, challengeEntity);

        // 챌린지 상태 정보 수정
        challengeEntity.setStatus(CANCELLED);

        return challengeRepository.save(challengeEntity).toResponseDto();
    }

    private Long getUserId(String token) throws Exception {
        MemberDto memberDto = memberFeignClient.userInfo(token).getBody();
        return memberDto.getId();
    }
}

