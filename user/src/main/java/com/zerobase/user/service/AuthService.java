//package com.zerobase.user.service;
//
//import com.zerobase.user.dto.JwtResponse;
//import com.zerobase.user.dto.LoginForm;
//import com.zerobase.user.exception.CustomException;
//import com.zerobase.user.exception.ErrorCode;
//import com.zerobase.user.util.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final AuthenticationManager authenticationManager;
//    private final UserService userService;
//    private final JwtUtil jwtUtil;
//
//    public JwtResponse authenticate(LoginForm loginForm) throws Exception {
//        //loginId와 비밀번호 일치여부 확인 (불일치 시 예외 발생)
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginForm.getLoginId(), loginForm.getPassword())
//            );
//        } catch (AuthenticationException e) {
//            throw new CustomException(ErrorCode.INVALID_LOGIN);
//        }
//
//        final UserDetails userDetails = userService.loadUserByUsername(loginForm.getLoginId());
//        List<GrantedAuthority> authorities =
//                new ArrayList<>(userDetails.getAuthorities());
//        final String jwt =
//                jwtUtil.generateToken(userDetails.getUsername(), authorities);
//
//        return new JwtResponse(jwt);
//    }
//}
