package com.example.ecommerce.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ecommerce.member.dto.response.LoginHistoryResponse;
import com.example.ecommerce.member.entity.LoginLog;
import com.example.ecommerce.member.mapper.LoginLogMapper;
import com.example.ecommerce.member.service.LoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志服务实现类
 */
@Slf4j
@Service
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    @Autowired
    public LoginLogServiceImpl(LoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    @Override
    public List<LoginHistoryResponse> getLoginHistory(Long memberId, Integer limit) {
        // 查询登录记录,按时间倒序
        LambdaQueryWrapper<LoginLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginLog::getMemberId, memberId)
                .orderByDesc(LoginLog::getLoginTime)
                .last("LIMIT " + (limit != null ? limit : 10));

        List<LoginLog> loginLogs = loginLogMapper.selectList(queryWrapper);

        // 转换为响应DTO并脱敏
        return loginLogs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 转换为响应DTO
     *
     * @param loginLog 登录记录实体
     * @return 响应DTO
     */
    private LoginHistoryResponse convertToResponse(LoginLog loginLog) {
        LoginHistoryResponse response = new LoginHistoryResponse();
        response.setLoginTime(loginLog.getLoginTime());
        response.setLoginType(loginLog.getLoginType());
        response.setLoginIp(maskIp(loginLog.getLoginIp()));
        response.setIpRegion(loginLog.getIpRegion());
        response.setDeviceType(loginLog.getDeviceType());
        response.setLoginStatus(loginLog.getLoginStatus());
        response.setFailReason(loginLog.getFailReason());
        return response;
    }

    /**
     * IP地址脱敏
     *
     * @param ip IP地址
     * @return 脱敏后的IP地址
     */
    private String maskIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return ip;
        }
        // IPv4脱敏: 192.168.1.100 -> 192.168.1.*
        if (ip.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
            int lastDotIndex = ip.lastIndexOf('.');
            return ip.substring(0, lastDotIndex + 1) + "*";
        }
        return ip;
    }
}
