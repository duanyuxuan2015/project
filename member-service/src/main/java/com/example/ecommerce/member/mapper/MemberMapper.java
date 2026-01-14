package com.example.ecommerce.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员Mapper接口
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}
