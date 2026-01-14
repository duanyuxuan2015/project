package com.example.ecommerce.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ecommerce.member.dto.request.UpdateMemberRequest;
import com.example.ecommerce.member.dto.response.AddressResponse;
import com.example.ecommerce.member.dto.response.LoginHistoryResponse;
import com.example.ecommerce.member.dto.response.MemberInfoResponse;
import com.example.ecommerce.member.entity.Address;
import com.example.ecommerce.member.entity.Member;
import com.example.ecommerce.member.exception.BusinessException;
import com.example.ecommerce.member.mapper.MemberMapper;
import com.example.ecommerce.member.service.AddressService;
import com.example.ecommerce.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 会员服务实现类
 */
@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final AddressService addressService;

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper, AddressService addressService) {
        this.memberMapper = memberMapper;
        this.addressService = addressService;
    }

    @Override
    public MemberInfoResponse getMemberInfo(Long memberId) {
        // 查询会员信息
        Member member = memberMapper.selectById(memberId);

        if (member == null) {
            throw new BusinessException("会员不存在");
        }

        // 转换为响应DTO并脱敏
        MemberInfoResponse response = new MemberInfoResponse();
        response.setMemberId(member.getId());
        response.setPhone(maskPhone(member.getPhone()));
        response.setNickname(member.getNickname());
        response.setAvatar(member.getAvatar());
        response.setGender(member.getGender());
        response.setBirthday(member.getBirthday());
        response.setRegisterType(member.getRegisterType());
        response.setRegisterTime(member.getRegisterTime());
        response.setAccountStatus(member.getAccountStatus());
        response.setLastLoginTime(member.getLastLoginTime());
        response.setLastLoginIp(maskIp(member.getLastLoginIp()));

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberInfo(Long memberId, UpdateMemberRequest request) {
        // 查询会员信息
        Member member = memberMapper.selectById(memberId);

        if (member == null) {
            throw new BusinessException("会员不存在");
        }

        // 更新字段(只更新非空字段)
        boolean updated = false;

        if (request.getNickname() != null) {
            member.setNickname(request.getNickname());
            updated = true;
        }

        if (request.getGender() != null) {
            member.setGender(request.getGender());
            updated = true;
        }

        if (request.getBirthday() != null) {
            member.setBirthday(request.getBirthday());
            updated = true;
        }

        if (updated) {
            int rows = memberMapper.updateById(member);
            if (rows == 0) {
                throw new BusinessException("更新会员信息失败");
            }
            log.info("会员信息更新成功: memberId={}", memberId);
        }
    }

    @Override
    public List<LoginHistoryResponse> getLoginHistory(Long memberId, Integer limit) {
        // TODO: 实现登录记录查询
        // 这个方法需要调用 LoginLogService
        throw new BusinessException("功能待实现");
    }

    @Override
    public List<AddressResponse> getAddresses(Long memberId) {
        return addressService.getAddresses(memberId);
    }

    /**
     * 手机号脱敏
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
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
