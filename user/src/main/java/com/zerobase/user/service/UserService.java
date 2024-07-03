package com.zerobase.user.service;

import com.zerobase.user.entity.MemberEntity;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        MemberEntity memberEntity = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)
                );

        List<GrantedAuthority> authorities = memberEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return User.builder()
                .username(memberEntity.getLoginId())
                .password(memberEntity.getPassword())
                .authorities(authorities)
                .build();
    }
}
