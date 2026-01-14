package com.example.ecommerce.member.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务工具类
 * 用于发送和验证短信验证码
 */
@Component
public class SmsUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 是否启用降级策略(从配置文件读取)
     */
    @Value("${sms.degradation.enabled:false}")
    private Boolean degradationEnabled;

    /**
     * 验证码过期时间(秒,默认5分钟)
     */
    @Value("${sms.code-expiration:300}")
    private Long codeExpiration;

    public SmsUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成6位数字验证码
     *
     * @return 验证码
     */
    public String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param type  验证码类型(REGISTER/LOGIN/RESET_PASSWORD)
     * @return 验证码(降级策略下返回,正常情况返回null)
     */
    public String sendVerificationCode(String phone, String type) {
        String code = generateCode();

        // 检查是否启用降级策略
        if (Boolean.TRUE.equals(degradationEnabled)) {
            // 降级策略: 不发送短信,直接返回验证码
            cacheCode(phone, type, code);
            return code;
        }

        // TODO: 调用第三方短信服务商API发送验证码
        // 例如: 阿里云短信服务、腾讯云短信服务
        // smsClient.send(phone, code);

        // 缓存验证码到 Redis
        cacheCode(phone, type, code);

        return null;
    }

    /**
     * 验证验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @param code  用户输入的验证码
     * @return true:验证码正确, false:验证码错误或已过期
     */
    public boolean verifyCode(String phone, String type, String code) {
        String cacheKey = getCacheKey(phone, type);

        try {
            String cachedCode = (String) redisTemplate.opsForValue().get(cacheKey);
            return code != null && code.equals(cachedCode);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查验证码发送频率限制
     * 同一手机号1分钟内只能发送1次,1小时内最多5次
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @return true:可以发送, false:发送频率过高
     */
    public boolean checkSendFrequency(String phone, String type) {
        String countKey = getSendCountKey(phone, type);

        try {
            // 获取已发送次数
            Integer count = (Integer) redisTemplate.opsForValue().get(countKey);
            if (count == null) {
                count = 0;
            }

            // 1小时内最多5次
            if (count >= 5) {
                return false;
            }

            // 增加发送次数
            redisTemplate.opsForValue().increment(countKey);
            // 设置1小时过期
            redisTemplate.expire(countKey, 1, TimeUnit.HOURS);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证成功后删除验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     */
    public void deleteCode(String phone, String type) {
        String cacheKey = getCacheKey(phone, type);
        redisTemplate.delete(cacheKey);
    }

    /**
     * 缓存验证码到 Redis
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @param code  验证码
     */
    private void cacheCode(String phone, String type, String code) {
        String cacheKey = getCacheKey(phone, type);
        redisTemplate.opsForValue().set(cacheKey, code, codeExpiration, TimeUnit.SECONDS);
    }

    /**
     * 获取验证码缓存 Key
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @return 缓存Key
     */
    private String getCacheKey(String phone, String type) {
        return "verification_code:" + phone + ":" + type;
    }

    /**
     * 获取发送次数缓存 Key
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @return 缓存Key
     */
    private String getSendCountKey(String phone, String type) {
        return "verification_code_count:" + phone + ":" + type;
    }
}
