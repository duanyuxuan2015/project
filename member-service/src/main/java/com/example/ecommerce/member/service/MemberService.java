package com.example.ecommerce.member.service;

import com.example.ecommerce.member.dto.request.UpdateMemberRequest;
import com.example.ecommerce.member.dto.response.AddressResponse;
import com.example.ecommerce.member.dto.response.LoginHistoryResponse;
import com.example.ecommerce.member.dto.response.MemberInfoResponse;

import java.util.List;

/**
 * 会员服务接口
 */
public interface MemberService {

    /**
     * 获取会员信息
     *
     * @param memberId 会员ID
     * @return 会员信息
     */
    MemberInfoResponse getMemberInfo(Long memberId);

    /**
     * 更新会员信息
     *
     * @param memberId 会员ID
     * @param request  更新请求
     */
    void updateMemberInfo(Long memberId, UpdateMemberRequest request);

    /**
     * 获取登录记录
     *
     * @param memberId 会员ID
     * @param limit    限制数量
     * @return 登录记录列表
     */
    List<LoginHistoryResponse> getLoginHistory(Long memberId, Integer limit);

    /**
     * 获取收货地址列表
     *
     * @param memberId 会员ID
     * @return 收货地址列表
     */
    List<AddressResponse> getAddresses(Long memberId);
}
