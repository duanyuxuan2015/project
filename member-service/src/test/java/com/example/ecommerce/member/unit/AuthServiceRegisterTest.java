package com.example.ecommerce.member.unit;

import com.example.ecommerce.member.dto.request.RegisterRequest;
import com.example.ecommerce.member.dto.response.RegisterResponse;
import com.example.ecommerce.member.entity.Member;
import com.example.ecommerce.member.enums.ResponseCode;
import com.example.ecommerce.member.exception.BusinessException;
import com.example.ecommerce.member.mapper.MemberMapper;
import com.example.ecommerce.member.service.AuthService;
import com.example.ecommerce.member.service.impl.AuthServiceImpl;
import com.example.ecommerce.member.service.VerificationCodeService;
import com.example.ecommerce.member.util.JwtUtil;
import com.example.ecommerce.member.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 认证服务单元测试 - 注册功能
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceRegisterTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private PasswordUtil passwordUtil;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest();
        validRequest.setPhone("13800138000");
        validRequest.setPassword("Password123");
        validRequest.setVerificationCode("123456");
        validRequest.setNickname("测试用户");
    }

    @Test
    void testRegister_Success() {
        // Given
        when(memberMapper.selectByPhone(anyString())).thenReturn(null);
        when(verificationCodeService.verifyCode(anyString(), anyString(), anyString())).thenReturn(true);
        when(passwordUtil.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(passwordUtil.isValidPhone(anyString())).thenReturn(true);
        when(passwordUtil.isStrongPassword(anyString())).thenReturn(true);
        when(memberMapper.insert(any(Member.class))).thenReturn(1);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("jwt-token");

        // When
        RegisterResponse response = authService.register(validRequest);

        // Then
        assertNotNull(response);
        assertEquals("13800138000", response.getPhone());
        assertEquals("测试用户", response.getNickname());
        assertEquals("jwt-token", response.getToken());

        verify(memberMapper, times(1)).insert(any(Member.class));
        verify(verificationCodeService, times(1)).verifyCode(eq("13800138000"), eq("REGISTER"), eq("123456"));
    }

    @Test
    void testRegister_PhoneAlreadyExists() {
        // Given
        Member existingMember = new Member();
        existingMember.setId(1L);
        existingMember.setPhone("13800138000");
        when(memberMapper.selectByPhone(anyString())).thenReturn(existingMember);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(validRequest);
        });

        assertEquals(ResponseCode.PHONE_ALREADY_EXISTS.getCode(), exception.getCode());
        verify(memberMapper, never()).insert(any(Member.class));
    }

    @Test
    void testRegister_VerificationCodeInvalid() {
        // Given
        when(memberMapper.selectByPhone(anyString())).thenReturn(null);
        when(verificationCodeService.verifyCode(anyString(), anyString(), anyString())).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(validRequest);
        });

        assertEquals(ResponseCode.VERIFICATION_CODE_ERROR.getCode(), exception.getCode());
        verify(memberMapper, never()).insert(any(Member.class));
    }

    @Test
    void testRegister_InvalidPhone() {
        // Given
        validRequest.setPhone("12345");
        when(passwordUtil.isValidPhone(anyString())).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(validRequest);
        });

        assertEquals(ResponseCode.BAD_REQUEST.getCode(), exception.getCode());
    }

    @Test
    void testRegister_WeakPassword() {
        // Given
        validRequest.setPassword("123");
        when(passwordUtil.isValidPhone(anyString())).thenReturn(true);
        when(passwordUtil.isStrongPassword(anyString())).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(validRequest);
        });

        assertEquals(ResponseCode.BAD_REQUEST.getCode(), exception.getCode());
    }
}
