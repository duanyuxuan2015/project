package com.example.ecommerce.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.member.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验证码Mapper接口
 */
@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
}
