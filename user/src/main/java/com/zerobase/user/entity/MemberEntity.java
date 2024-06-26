package com.zerobase.user.entity;

import com.zerobase.user.enums.MemberLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private String nickName;
    private String name;
    private LocalDate birth;
    private String job;
    private String interests;
    private String gender;
    private Long points;
    private String provider;
    private String providerId;
    @Enumerated(EnumType.STRING)
    private MemberLevel level;
}
