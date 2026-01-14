package com.example.ecommerce.member.service;

import com.example.ecommerce.member.dto.response.LoginHistoryResponse;

import java.util.List;

/**
 * 登录日志服务接口
 */
public interface LoginLogService {

    /**
     * 获取登录记录
     *
     * @param memberId 会员ID
     * @param limit    限制数量
     * @return 登录记录列表
     */
    List<LoginHistoryResponse> getLoginHistory(Long memberId, Integer limit);
}
