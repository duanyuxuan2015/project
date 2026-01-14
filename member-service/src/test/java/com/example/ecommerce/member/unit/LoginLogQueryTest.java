package com.example.ecommerce.member.unit;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ecommerce.member.dto.response.LoginHistoryResponse;
import com.example.ecommerce.member.entity.LoginLog;
import com.example.ecommerce.member.mapper.LoginLogMapper;
import com.example.ecommerce.member.service.impl.LoginLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 登录记录查询单元测试
 */
@ExtendWith(MockitoExtension.class)
class LoginLogQueryTest {

    @Mock
    private LoginLogMapper loginLogMapper;

    @InjectMocks
    private LoginLogServiceImpl loginLogService;

    private LoginLog testLog1;
    private LoginLog testLog2;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        testLog1 = new LoginLog();
        testLog1.setId(1L);
        testLog1.setMemberId(1L);
        testLog1.setLoginType("PASSWORD");
        testLog1.setLoginTime(LocalDateTime.now().minusDays(1));
        testLog1.setLoginIp("192.168.1.100");
        testLog1.setIpRegion("北京市朝阳区");
        testLog1.setDeviceType("PC");
        testLog1.setLoginStatus(1);

        testLog2 = new LoginLog();
        testLog2.setId(2L);
        testLog2.setMemberId(1L);
        testLog2.setLoginType("SMS");
        testLog2.setLoginTime(LocalDateTime.now());
        testLog2.setLoginIp("210.21.1.1");
        testLog2.setIpRegion("上海市浦东新区");
        testLog2.setDeviceType("iOS");
        testLog2.setLoginStatus(1);
    }

    @Test
    void testGetLoginHistory_Success() {
        // Given
        List<LoginLog> logs = Arrays.asList(testLog2, testLog1); // 按时间倒序
        when(loginLogMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(logs);

        // When
        List<LoginHistoryResponse> responses = loginLogService.getLoginHistory(1L, 10);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        // 验证按时间倒序排列
        assertEquals("SMS", responses.get(0).getLoginType());
        assertEquals("PASSWORD", responses.get(1).getLoginType());

        // 验证IP脱敏
        assertEquals("210.21.*.*", responses.get(0).getLoginIp());
    }

    @Test
    void testGetLoginHistory_WithLimit() {
        // Given
        when(loginLogMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(testLog2));

        // When
        List<LoginHistoryResponse> responses = loginLogService.getLoginHistory(1L, 1);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void testGetLoginHistory_Empty() {
        // Given
        when(loginLogMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        // When
        List<LoginHistoryResponse> responses = loginLogService.getLoginHistory(1L, 10);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testGetLoginHistory_WithFailures() {
        // Given: 包含登录失败记录
        LoginLog failedLog = new LoginLog();
        failedLog.setId(3L);
        failedLog.setMemberId(1L);
        failedLog.setLoginType("PASSWORD");
        failedLog.setLoginTime(LocalDateTime.now());
        failedLog.setLoginIp("192.168.1.200");
        failedLog.setLoginStatus(2); // 失败
        failedLog.setFailReason("密码错误");

        when(loginLogMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(failedLog));

        // When
        List<LoginHistoryResponse> responses = loginLogService.getLoginHistory(1L, 10);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(2, responses.get(0).getLoginStatus());
        assertEquals("密码错误", responses.get(0).getFailReason());
    }

    @Test
    void testGetLoginHistory_DefaultLimit() {
        // Given
        when(loginLogMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(testLog2));

        // When: 不传limit参数
        List<LoginHistoryResponse> responses = loginLogService.getLoginHistory(1L, null);

        // Then: 默认返回10条
        assertNotNull(responses);
        verify(loginLogMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }
}
