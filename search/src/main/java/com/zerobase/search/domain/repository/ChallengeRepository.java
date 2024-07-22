package com.zerobase.search.domain.repository;

import com.zerobase.search.domain.entity.ChallengeEntity;
import com.zerobase.search.domain.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {
    List<ChallengeEntity> findAllByUserId(Long userId);

    List<ChallengeEntity> findByStatus(ChallengeStatus challengeStatus);

    @Query("SELECT c" +
            " FROM challenge c " +
            "WHERE c.id = :id AND " +
            "c.userId = :userId AND " +
            "c.status <> 'COMPLETED' AND c.status <> 'EXPIRED'")
    Optional<ChallengeEntity> findUpdatableByIdAndUserId(Long id, Long userId);

    List<ChallengeEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<ChallengeEntity> findByIdAndUserId(Long id, Long userId);


    Optional<ChallengeEntity> findByIdAndUserIdAndStatusIn(
            Long challengeId, Long userId, List<ChallengeStatus> statuses
    );

    //검색
    @Query("SELECT c FROM challenge c WHERE " +
            "(:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:userNickName IS NULL OR LOWER(c.userNickName) = LOWER(:userNickName)) AND " +
            "(:status IS NULL OR c.status = :status)")
    List<ChallengeEntity> searchChallenges(@Param("title") String title,
                                           @Param("userNickName") String userNickName,
                                           @Param("status") ChallengeStatus status);

}
