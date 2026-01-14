package com.example.ecommerce.member.integration;

import com.example.ecommerce.member.dto.request.RegisterRequest;
import com.example.ecommerce.member.dto.response.RegisterResponse;
import com.example.ecommerce.member.entity.Member;
import com.example.ecommerce.member.enums.ResponseCode;
import com.example.ecommerce.member.exception.BusinessException;
import com.example.ecommerce.member.mapper.MemberMapper;
import com.example.ecommerce.member.service.VerificationCodeService;
import com.example.ecommerce.member.service.impl.AuthServiceImpl;
import com.example.ecommerce.member.util.JwtUtil;
import com.example.ecommerce.member.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 并发注册冲突测试
 * 集成测试，验证并发场景下的唯一索引约束处理
 */
@SpringBootTest
@ActiveProfiles("test")
class ConcurrentRegisterTest {

    @Autowired
    private AuthServiceImpl authService;

    @MockBean
    private MemberMapper memberMapper;

    @MockBean
    private VerificationCodeService verificationCodeService;

    @MockBean
    private PasswordUtil passwordUtil;

    @MockBean
    private JwtUtil jwtUtil;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setPhone("13800138000");
        request.setPassword("Password123");
        request.setVerificationCode("123456");
        request.setNickname("测试用户");

        // Mock默认行为
        lenient().when(passwordUtil.isValidPhone(anyString())).thenReturn(true);
        lenient().when(passwordUtil.isStrongPassword(anyString())).thenReturn(true);
        lenient().when(passwordUtil.encode(anyString())).thenReturn("$2a$10$encoded");
        lenient().when(verificationCodeService.verifyCode(anyString(), anyString(), anyString())).thenReturn(true);
        lenient().when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("jwt-token");
    }

    @Test
    void testConcurrentRegister_OnlyOneSucceeds() throws InterruptedException {
        // Given: 模拟数据库唯一索引约束
        // 第一次查询返回null(用户不存在)，第二次查询也返回null
        when(memberMapper.selectOne(any())).thenReturn(null);
        // 第一次插入成功，后续插入抛出异常模拟唯一索引冲突
        AtomicInteger insertCount = new AtomicInteger(0);
        when(memberMapper.insert(any(Member.class))).thenAnswer(invocation -> {
            int count = insertCount.incrementAndGet();
            if (count == 1) {
                return 1; // 第一个插入成功
            } else {
                throw new BusinessException(ResponseCode.PHONE_ALREADY_EXISTS); // 后续插入失败
            }
        });

        // When: 模拟10个并发注册请求
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // 等待所有线程准备就绪
                    RegisterResponse response = authService.register(request);
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    if (ResponseCode.PHONE_ALREADY_EXISTS.getCode().equals(e.getCode())) {
                        failureCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 启动所有线程
        startLatch.countDown();
        latch.await();

        // Then: 只有一个注册成功，其余都失败
        assertEquals(1, successCount.get(), "应该只有一个注册成功");
        assertEquals(threadCount - 1, failureCount.get(), "其余注册应该失败");

        executorService.shutdown();
    }

    @Test
    void testConcurrentRegister_DatabaseConstraint() throws InterruptedException {
        // Given: 模拟真实数据库唯一索引约束
        when(memberMapper.selectOne(any()))
                .thenReturn(null) // 第一次查询用户不存在
                .thenReturn(new Member()); // 第二次查询用户已存在

        when(memberMapper.insert(any(Member.class)))
                .thenReturn(1); // 插入成功

        // When: 3个线程并发注册
        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    RegisterResponse response = authService.register(request);
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        startLatch.countDown();
        latch.await();

        // Then: 验证结果
        assertTrue(successCount.get() + failureCount.get() == threadCount);
        assertTrue(successCount.get() >= 1, "至少有一个成功");

        executorService.shutdown();
    }
}
