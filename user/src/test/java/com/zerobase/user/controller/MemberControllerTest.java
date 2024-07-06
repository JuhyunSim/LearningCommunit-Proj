package com.zerobase.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.user.dto.MemberDto;
import com.zerobase.user.dto.RegisterForm;
import com.zerobase.user.entity.MemberEntity;
import com.zerobase.user.enums.Role;
import com.zerobase.user.service.MemberService;
import com.zerobase.user.util.AESUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AESUtil aesUtil;

    @Test
    void registerUser_ValidInput_ShouldReturnOk() throws Exception {
        RegisterForm registerForm = RegisterForm.builder()
                .username("testUser")
                .password("Test@1234")
                .email("test@example.com")
                .phoneNumber("01012345678")
                .nickName("TestNick")
                .name("Test Name")
                .birth(LocalDate.of(1990, 1, 1))
                .build();

        MemberEntity memberEntity = MemberEntity.builder()
                .loginId(registerForm.getUsername())
                .password(registerForm.getPassword())
                .email(registerForm.getEmail())
                .phoneNumber(registerForm.getPhoneNumber())
                .nickName(registerForm.getNickName())
                .name(registerForm.getName())
                .birth(registerForm.getBirth())
                .job(registerForm.getJob())
                .interests(registerForm.getInterests())
                .gender(registerForm.getGender())
                .roles(List.of(Role.USER))
                .build();;

        MemberDto memberDto = memberEntity.toDto(aesUtil);
        when(memberService.registerUser(any(RegisterForm.class))).thenReturn(memberDto);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerForm))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value(registerForm.getUsername()))
                .andExpect(jsonPath("$.password").value(registerForm.getPassword()))
                .andExpect(jsonPath("$.email").value(registerForm.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(registerForm.getPhoneNumber()))
                .andExpect(jsonPath("$.nickName").value(registerForm.getNickName()))
                .andExpect(jsonPath("$.name").value(registerForm.getName()))
                .andExpect(jsonPath("$.birth").value(registerForm.getBirth().toString()))
                .andExpect(jsonPath("$.job").value(registerForm.getJob()))
                .andExpect(jsonPath("$.interests").value(registerForm.getInterests()))
                .andExpect(jsonPath("$.gender").doesNotExist())
                .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    @Test
    void registerUser_InvalidInput_ShouldReturnBadRequest() throws Exception {
        RegisterForm registerForm = RegisterForm.builder()
                .username("")  // Invalid loginId
                .password("short")
                .email("invalid-email")
                .phoneNumber("invalid-phone")
                .nickName("")
                .name("")
                .birth(LocalDate.of(2030, 1, 1))  // Future date
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerForm))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_InvalidPassword_ShouldReturnBadRequest() throws Exception {
        RegisterForm registerForm = RegisterForm.builder()
                .username("LoginTest1234")  // Invalid loginId
                .password("short")
                .email("invalid-email")
                .phoneNumber("invalid-phone")
                .nickName("")
                .name("")
                .birth(LocalDate.of(2030, 1, 1))  // Future date
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerForm))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_InvalidEmail_ShouldReturnBadRequest() throws Exception {
        RegisterForm registerForm = RegisterForm.builder()
                .username("LoginTest1234")  // Invalid loginId
                .password("Test1234!@")
                .email("invalid-email")
                .phoneNumber("invalid-phone")
                .nickName("")
                .name("")
                .birth(LocalDate.of(2030, 1, 1))  // Future date
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerForm))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_InvalidPhone_ShouldReturnBadRequest() throws Exception {
        RegisterForm registerForm = RegisterForm.builder()
                .username("LoginTest1234")  // Invalid loginId
                .password("Test1234!@")
                .email("test@example.com")
                .phoneNumber("invalid-phone")
                .nickName("")
                .name("")
                .birth(LocalDate.of(2030, 1, 1))  // Future date
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerForm))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}