package com.example.ecommerce.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.member.entity.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址Mapper接口
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
