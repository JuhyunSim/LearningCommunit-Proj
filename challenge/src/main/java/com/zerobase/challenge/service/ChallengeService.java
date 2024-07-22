package com.zerobase.challenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zerobase.challenge.client.MemberFeignClient;
import com.zerobase.challenge.domain.dto.ChallengeResponseDto;
import com.zerobase.challenge.domain.dto.CreateChallengeForm;
import com.zerobase.challenge.domain.dto.MemberDto;
import com.zerobase.challenge.domain.dto.UpdateChallengeForm;
import com.zerobase.challenge.domain.entity.ChallengeEntity;
import com.zerobase.challenge.domain.enums.ChallengeStatus;
import com.zerobase.challenge.domain.repository.ChallengeRepository;
import com.zerobase.challenge.exception.CustomException;
import com.zerobase.challenge.exception.ErrorCode;
import com.zerobase.kafka.dto.KafkaChallengeEventDto;
import com.zerobase.kafka.enums.KafkaTopic;
import com.zerobase.kafka.producer.ChallengeEventSender;
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
    private final ChallengeEventSender challengeEventSender;

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
        // Kafka 이벤트 발행
        ChallengeEntity savedChallenge = challengeRepository.save(challengeEntity);

        sendChallengeEvent(KafkaTopic.CHALLENGE_CREATED, savedChallenge);

        return savedChallenge.toChallengeDto();
    }

    @Scheduled(cron = "0 0 1 * * ?") // 매일 밤 1시에 실행
    public void updateExpiredChallenges() {
        List<ChallengeEntity> ongoingChallenges = challengeRepository.findByStatus(ONGOING);
        ongoingChallenges.stream()
                .filter(challenge -> challenge.getDueDate().isBefore(LocalDate.now()))
                .forEach(challenge -> {
                    challenge.setStatus(ChallengeStatus.EXPIRED);
                    ChallengeEntity updatedEntity = challengeRepository.save(challenge);
                    // Kafka 이벤트 발행
                    try {
                        sendChallengeEvent(KafkaTopic.CHALLENGE_UPDATED, updatedEntity);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    //챌린지 내용 수정
    public ChallengeResponseDto updateChallenge(
            String token,
            UpdateChallengeForm updateChallengeForm
    ) throws Exception {
        //토큰 소유자와 요청 form의 유저아이디가 일치해야 함
        //기간 만료 똔ㄴ 완료 상태가 아닌 챌린지
        ChallengeEntity challengeEntity =
                challengeRepository.findUpdatableByIdAndUserId(
                        updateChallengeForm.getChallengeId(), getUserId(token))
                        .orElseThrow(
                                () -> new CustomException(ErrorCode.UPDATABLE_CHALLENGE_NOT_FOUND)
                        );

        //duedate & status 검증
        challengeFormValidator.validateUpdateChallengeForm(
                challengeEntity, updateChallengeForm
        );

        //엔티티 수정 (chaellengeId, userId, username은 기존 것으로 사용, 변경 불가)
        ChallengeEntity updatedChallengeEntity =
                updateChallengeForm.toUpdatedChallengeEntity(
                        challengeEntity.getId(),
                        challengeEntity.getUserId(),
                        challengeEntity.getUserNickName(),
                        challengeEntity.getStatus()
                );

        ChallengeEntity updatedChallenge =
                challengeRepository.save(updatedChallengeEntity);
        // Kafka 이벤트 발행
        sendChallengeEvent(KafkaTopic.CHALLENGE_UPDATED, updatedChallenge);
        return updatedChallenge.toChallengeDto();
    }

    //챌린지 취소
    public ChallengeResponseDto cancelStatus(
            String token, Long challengeId
            ) throws Exception {
        //토큰 소유자와 요청 챌린지 게시물의 유저아이디가 일치해야 함
        //기간 만료 또는 완료 상태가 아닌 챌린지
        ChallengeEntity challengeEntity = challengeRepository
                .findUpdatableByIdAndUserId(challengeId, getUserId(token))
                .orElseThrow(
                        () -> new CustomException(ErrorCode.UPDATABLE_CHALLENGE_NOT_FOUND)
                );

        // 챌린지 상태 정보 수정
        challengeEntity.setStatus(CANCELLED);
        //저장
        ChallengeEntity savedEntity = challengeRepository.save(challengeEntity);
        // Kafka 이벤트 발행
        sendChallengeEvent(KafkaTopic.CHALLENGE_UPDATED, savedEntity);

        return savedEntity.toChallengeDto();
    }

    //본인 챌린지 리스트 조회(간단조회)
    public List<ChallengeResponseDto.ChallengeSimpleDto> getChallenges(
            String token) throws Exception {
        Long userId = getUserId(token);
        List<ChallengeEntity> challengeEntities =
                challengeRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return challengeEntities.stream()
                .map(ChallengeEntity::toChallengeSimpleDto)
                .toList();
    }

    //챌린지 조회(상세정보 조회)
    public ChallengeResponseDto getChallenge(String token, Long challengeId) throws Exception {
        Long userId = getUserId(token);
        //본인 챌린지 조회
        ChallengeEntity challengeEntity =
                challengeRepository.findByIdAndUserId(challengeId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.CHALLENGE_NOT_FOUND)
        );

        return challengeEntity.toChallengeDto();
    }

    public void deleteChallenge(String token, Long challengeId) throws Exception {
        //취소, 완료, 대기 상태인 챌린지만 삭제할 수 있음
        //본인 챌린지만 삭제할 수 있음
        List<ChallengeStatus> statuses =
                List.of(CANCELLED, COMPLETED, PENDING);
        Long userId = getUserId(token);
        ChallengeEntity challengeEntity =
                challengeRepository.findByIdAndUserIdAndStatusIn(
                        challengeId, userId, statuses).orElseThrow(
                        () -> new CustomException(ErrorCode.DELETABLE_CHALLENGE_NOT_FOUND)
                );
        challengeRepository.delete(challengeEntity);
        //카프카 발행
        sendChallengeEvent(KafkaTopic.CHALLENGE_DELETED, challengeEntity);
    }

    private Long getUserId(String token) throws Exception {
        MemberDto memberDto = memberFeignClient.userInfo(token).getBody();
        return memberDto.getId();
    }

    private void sendChallengeEvent(
            KafkaTopic kafkaTopic, ChallengeEntity challengeEntity
    ) throws JsonProcessingException {
        KafkaChallengeEventDto eventDto =challengeEntity.toKafkaChallengeEventDto();
        challengeEventSender.sendChallengeEvent(kafkaTopic, eventDto);
    }
}

