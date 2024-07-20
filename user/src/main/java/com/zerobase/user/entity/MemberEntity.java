package com.zerobase.user.entity;

import com.zerobase.user.dto.MemberDto;
import com.zerobase.user.enums.Gender;
import com.zerobase.user.enums.MemberLevel;
import com.zerobase.user.enums.Provider;
import com.zerobase.user.enums.Role;
import com.zerobase.user.util.AESUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
public class MemberEntity extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
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
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private List<Role> roles;

    @Builder
    MemberEntity(
            String username,
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
            MemberLevel level,
            List<Role> roles
    ) {
        this.username = username;
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
        this.roles = roles != null ? roles : List.of(Role.ROLE_USER);
    }

    public MemberDto toDto(AESUtil aesUtil) throws Exception {
        return MemberDto.builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .phoneNumber(aesUtil.decrypt(this.phoneNumber))
                .nickName(this.nickName)
                .name(this.name)
                .birth(this.birth)
                .job(this.job)
                .interests(this.interests)
                .gender(this.gender)
                .points(this.points)
                .level(this.level)
                .roles(this.roles)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
