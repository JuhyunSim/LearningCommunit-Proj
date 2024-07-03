package com.zerobase.user.entity;

import com.zerobase.user.enums.Gender;
import com.zerobase.user.enums.MemberLevel;
import com.zerobase.user.enums.Provider;
import com.zerobase.user.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loginId;
    private String password;
    private String email;
    private String phoneNumber;
    private String nickName;
    private String name;
    private LocalDate birth;
    private String job;
    private String interests;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Long points;
    @Enumerated(EnumType.STRING)
    private Provider provider;
    private String providerId;
    @Enumerated(EnumType.STRING)
    private MemberLevel level;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Role> roles = List.of(Role.USER);

    @Builder
    MemberEntity(
        String loginId,
        String password,
        String email,
        String nickName,
        String name,
        LocalDate birth,
        String job,
        String interests,
        Gender gender,
        Long points,
        Provider provider,
        String providerId,
        MemberLevel level
    ) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickName = nickName;
        this.name = name;
        this.birth = birth;
        this.job = job;
        this.interests = interests;
        this.gender = gender;
        this.points = points;
        this.provider = provider;
        this.providerId = providerId;
        this.level = level;
    }
}
