package com.example.ecommerce.member.unit;

import com.example.ecommerce.member.dto.request.SendCodeRequest;
import com.example.ecommerce.member.service.impl.VerificationCodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 验证码服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private VerificationCodeServiceImpl verificationCodeService;

    private SendCodeRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new SendCodeRequest();
        validRequest.setPhone("13800138000");
        validRequest.setType("REGISTER");

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testSendCode_Success() {
        // Given
        when(valueOperations.get(anyString())).thenReturn(null);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        // When
        String code = verificationCodeService.sendCode(validRequest);

        // Then
        assertNotNull(code);
        assertTrue(code.matches("\\d{6}"));
        verify(valueOperations, times(1)).set(anyString(), anyString(), eq(300L), eq(TimeUnit.SECONDS));
        verify(valueOperations, times(1)).increment(anyString());
    }

    @Test
    void testSendCode_TooFrequent() {
        // Given
        when(valueOperations.get(anyString())).thenReturn(5);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            verificationCodeService.sendCode(validRequest);
        });

        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void testVerifyCode_Success() {
        // Given
        when(valueOperations.get(anyString())).thenReturn("123456");

        // When
        boolean result = verificationCodeService.verifyCode("13800138000", "REGISTER", "123456");

        // Then
        assertTrue(result);
    }

    @Test
    void testVerifyCode_InvalidCode() {
        // Given
        when(valueOperations.get(anyString())).thenReturn("123456");

        // When
        boolean result = verificationCodeService.verifyCode("13800138000", "REGISTER", "000000");

        // Then
        assertFalse(result);
    }

    @Test
    void testVerifyCode_Expired() {
        // Given
        when(valueOperations.get(anyString())).thenReturn(null);

        // When
        boolean result = verificationCodeService.verifyCode("13800138000", "REGISTER", "123456");

        // Then
        assertFalse(result);
    }
}
