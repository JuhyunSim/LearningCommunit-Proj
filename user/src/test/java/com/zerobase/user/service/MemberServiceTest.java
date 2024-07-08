package com.zerobase.user.service;

import com.zerobase.user.dto.MemberDto;
import com.zerobase.user.dto.RegisterForm;
import com.zerobase.user.entity.MemberEntity;
import com.zerobase.user.enums.Gender;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.repository.MemberRepository;
import com.zerobase.user.util.AESUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AESUtil aesUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    void testRegisterUser_Success() throws Exception {
        RegisterForm form = RegisterForm.builder()
                .username("testuser")
                .password("Test@1234")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .nickName("testnick")
                .name("testname")
                .birth(LocalDate.of(1990, 1, 1))
                .job("Developer")
                .interests("Coding")
                .gender(Gender.MALE)
                .build();

        when(memberRepository.existsByUsername(form.getUsername())).thenReturn(false);
        when(memberRepository.existsByEmail(form.getEmail())).thenReturn(false);
        when(aesUtil.encrypt(form.getPhoneNumber())).thenReturn("encryptedPhoneNumber");
        when(memberRepository.existsByPhoneNumber("encryptedPhoneNumber")).thenReturn(false);

        MemberEntity savedMember = form.toEntity(passwordEncoder, aesUtil);
        MemberDto memberDto = savedMember.toDto(aesUtil);
        when(memberRepository.save(any())).thenReturn(savedMember);
        when(aesUtil.decrypt("encryptedPhoneNumber")).thenReturn("1234567890");

        //when
        MemberDto result = memberService.registerUser(form);

        //then
        assertNotNull(result);
        assertEquals(form.getUsername(), result.getUsername());
        assertEquals(form.getEmail(), result.getEmail());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("testnick", result.getNickName());
        assertEquals(Gender.MALE, result.getGender());
        assertEquals("Coding", result.getInterests());
        assertEquals(LocalDate.of(1990, 1, 1), result.getBirth());
        assertEquals("Developer", result.getJob());
    }

    @Test
    void testRegisterUser_DuplicateLoginId() {
        //then
        RegisterForm form = RegisterForm.builder()
                .username("testuser")
                .password("Test@1234")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .nickName("testnick")
                .name("testname")
                .birth(LocalDate.of(1990, 1, 1))
                .job("Developer")
                .interests("Coding")
                .gender(Gender.MALE)
                .build();

        when(memberRepository.existsByUsername(form.getUsername())).thenReturn(true);
        //when
        Exception exception = assertThrows(CustomException.class, () -> {
            memberService.registerUser(form);
        });
        //then
        String expectedMessage = "이미 존재하는 아이디입니다.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        //then
        RegisterForm form = RegisterForm.builder()
                .username("testuser")
                .password("Test@1234")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .nickName("testnick")
                .name("testname")
                .birth(LocalDate.of(1990, 1, 1))
                .job("Developer")
                .interests("Coding")
                .gender(Gender.MALE)
                .build();

        when(memberRepository.existsByEmail(form.getEmail())).thenReturn(true);
        //when
        Exception exception = assertThrows(CustomException.class, () -> {
            memberService.registerUser(form);
        });
        //then
        String expectedMessage = "해당 이메일로 가입한 내역이 있습니다.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testRegisterUser_DuplicatePhoneNumber() throws Exception {
        //then
        RegisterForm form = RegisterForm.builder()
                .username("testuser")
                .password("Test@1234")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .nickName("testnick")
                .name("testname")
                .birth(LocalDate.of(1990, 1, 1))
                .job("Developer")
                .interests("Coding")
                .gender(Gender.MALE)
                .build();

        when(memberRepository.existsByPhoneNumber(aesUtil.encrypt(form.getPhoneNumber()))).thenReturn(true);
        //when
        Exception exception = assertThrows(CustomException.class, () -> {
            memberService.registerUser(form);
        });
        //then
        String expectedMessage = "해당 전화번호로 가입한 내역이 있습니다.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}