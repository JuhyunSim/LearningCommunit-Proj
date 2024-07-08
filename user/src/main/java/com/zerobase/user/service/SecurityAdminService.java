package com.zerobase.user.service;

import com.zerobase.user.entity.AdminEntity;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SecurityAdminService implements UserDetailsService {
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            log.debug("Load user by username: {}", username);
            AdminEntity adminEntity = adminRepository.findByUsername(username)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)
                    );

            List<GrantedAuthority> authorities = adminEntity.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .collect(Collectors.toList());

            return User.builder()
                    .username(adminEntity.getUsername())
                    .password(adminEntity.getPassword())
                    .authorities(authorities)
                    .build();
    }
}
