package com.example.ecommerce.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.member.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录记录Mapper接口
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}
