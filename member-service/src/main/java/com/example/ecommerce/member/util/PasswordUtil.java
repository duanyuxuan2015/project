package com.example.ecommerce.member.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类
 * 使用 BCrypt 算法加密和验证密码
 */
@Component
public class PasswordUtil {

    /**
     * 密码编码器
     */
    private final PasswordEncoder passwordEncoder;

    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 加密密码
     *
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 加密后的密码
     * @return true:密码匹配, false:密码不匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 验证密码强度
     * 密码规则: 8-20位,必须包含字母和数字
     *
     * @param password 密码
     * @return true:密码强度符合要求, false:密码强度不符合要求
     */
    public boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 20) {
            return false;
        }

        // 必须包含字母
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        // 必须包含数字
        boolean hasDigit = password.matches(".*\\d.*");

        return hasLetter && hasDigit;
    }

    /**
     * 验证验证码格式
     * 验证码规则: 6位数字
     *
     * @param code 验证码
     * @return true:验证码格式正确, false:验证码格式错误
     */
    public boolean isValidVerificationCode(String code) {
        return code != null && code.matches("\\d{6}");
    }

    /**
     * 验证手机号格式
     * 手机号规则: 11位数字,以13/14/15/17/18/19开头
     *
     * @param phone 手机号
     * @return true:手机号格式正确, false:手机号格式错误
     */
    public boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }
}
