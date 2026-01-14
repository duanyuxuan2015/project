package com.example.ecommerce.member.unit;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ecommerce.member.dto.request.PasswordLoginRequest;
import com.example.ecommerce.member.dto.request.SmsLoginRequest;
import com.example.ecommerce.member.dto.response.LoginResponse;
import com.example.ecommerce.member.entity.Member;
import com.example.ecommerce.member.enums.ResponseCode;
import com.example.ecommerce.member.exception.BusinessException;
import com.example.ecommerce.member.mapper.MemberMapper;
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
 * 认证服务单元测试 - 登录功能
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTest {

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

    private Member testMember;
    private PasswordLoginRequest passwordLoginRequest;
    private SmsLoginRequest smsLoginRequest;

    @BeforeEach
    void setUp() {
        // 创建测试会员
        testMember = new Member();
        testMember.setId(1L);
        testMember.setPhone("13800138000");
        testMember.setPassword("$2a$10$encodedPassword");
        testMember.setNickname("测试用户");
        testMember.setAccountStatus(1); // 正常状态

        // 密码登录请求
        passwordLoginRequest = new PasswordLoginRequest();
        passwordLoginRequest.setPhone("13800138000");
        passwordLoginRequest.setPassword("Password123");

        // 验证码登录请求
        smsLoginRequest = new SmsLoginRequest();
        smsLoginRequest.setPhone("13800138000");
        smsLoginRequest.setVerificationCode("123456");
    }

    @Test
    void testPasswordLogin_Success() {
        // Given
        when(memberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);
        when(passwordUtil.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("jwt-token");
        when(memberMapper.updateById(any(Member.class))).thenReturn(1);

        // When
        LoginResponse response = authService.passwordLogin(passwordLoginRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getMemberId());
        assertEquals("13800138000", response.getPhone());
        assertEquals("测试用户", response.getNickname());
        assertEquals("jwt-token", response.getToken());

        verify(memberMapper, times(1)).updateById(any(Member.class));
    }

    @Test
    void testPasswordLogin_PhoneNotRegistered() {
        // Given
        when(memberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.passwordLogin(passwordLoginRequest);
        });

        assertEquals(ResponseCode.PHONE_NOT_REGISTERED.getCode(), exception.getCode());
    }

    @Test
    void testPasswordLogin_PasswordIncorrect() {
        // Given
        when(memberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);
        when(passwordUtil.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.passwordLogin(passwordLoginRequest);
        });

        assertEquals(ResponseCode.PASSWORD_ERROR.getCode(), exception.getCode());
    }

    @Test
    void testPasswordLogin_AccountLocked() {
        // Given
        testMember.setAccountStatus(2); // 冻结状态
        when(memberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.passwordLogin(passwordLoginRequest);
        });

        assertEquals(ResponseCode.ACCOUNT_LOCKED.getCode(), exception.getCode());
    }

    @Test
    void testSmsLogin_Success() {
        // Given
        when(memberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);
        when(verificationCodeService.verifyCode(anyString(), anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("jwt-token");
        when(memberMapper.updateById(any(Member.class))).thenReturn(1);

        // When
        LoginResponse response = authService.smsLogin(smsLoginRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getMemberId());
        assertEquals("jwt-token", response.getToken());

        verify(memberMapper, times(1)).updateById(any(Member.class));
    }

    @Test
    void testSmsLogin_VerificationCodeInvalid() {
        // Given
        when(memberMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMember);
        when(verificationCodeService.verifyCode(anyString(), anyString(), anyString())).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.smsLogin(smsLoginRequest);
        });

        assertEquals(ResponseCode.VERIFICATION_CODE_ERROR.getCode(), exception.getCode());
    }
}
