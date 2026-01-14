package com.example.ecommerce.member.service.impl;

import com.example.ecommerce.member.dto.request.SendCodeRequest;
import com.example.ecommerce.member.enums.ResponseCode;
import com.example.ecommerce.member.exception.BusinessException;
import com.example.ecommerce.member.service.VerificationCodeService;
import com.example.ecommerce.member.util.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 */
@Slf4j
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final SmsUtil smsUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public VerificationCodeServiceImpl(SmsUtil smsUtil, RedisTemplate<String, Object> redisTemplate) {
        this.smsUtil = smsUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String sendCode(SendCodeRequest request) {
        String phone = request.getPhone();
        String type = request.getType();

        // 检查发送频率限制
        if (!smsUtil.checkSendFrequency(phone, type)) {
            throw new BusinessException(ResponseCode.VERIFICATION_CODE_SEND_TOO_FREQUENT);
        }

        // 发送验证码
        String code = smsUtil.sendVerificationCode(phone, type);

        log.info("发送验证码成功: phone={}, type={}, degradation={}", phone, type, code != null);

        return code;
    }

    @Override
    public boolean verifyCode(String phone, String type, String code) {
        boolean isValid = smsUtil.verifyCode(phone, type, code);

        if (isValid) {
            // 验证成功后删除验证码(一次性使用)
            smsUtil.deleteCode(phone, type);
            log.info("验证码验证成功: phone={}, type={}", phone, type);
        } else {
            log.warn("验证码验证失败: phone={}, type={}", phone, type);
        }

        return isValid;
    }
}
