package com.zerobase.user.repository;

import com.zerobase.user.entity.MemberEntity;
import com.zerobase.common.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmail(String email);

    Optional<MemberEntity> findByProviderAndProviderId(Provider provider, String providerId);

    Optional<MemberEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String encryptedPhoneNumber);

}
