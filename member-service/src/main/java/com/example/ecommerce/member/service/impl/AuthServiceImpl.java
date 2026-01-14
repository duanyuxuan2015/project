package com.example.ecommerce.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ecommerce.member.dto.request.PasswordLoginRequest;
import com.example.ecommerce.member.dto.request.RegisterRequest;
import com.example.ecommerce.member.dto.request.ResetPasswordRequest;
import com.example.ecommerce.member.dto.request.SmsLoginRequest;
import com.example.ecommerce.member.dto.request.ThirdPartyRegisterRequest;
import com.example.ecommerce.member.dto.response.LoginResponse;
import com.example.ecommerce.member.dto.response.RegisterResponse;
import com.example.ecommerce.member.entity.Member;
import com.example.ecommerce.member.enums.ResponseCode;
import com.example.ecommerce.member.exception.BusinessException;
import com.example.ecommerce.member.mapper.MemberMapper;
import com.example.ecommerce.member.producer.LoginLogProducer;
import com.example.ecommerce.member.service.AuthService;
import com.example.ecommerce.member.service.VerificationCodeService;
import com.example.ecommerce.member.util.JwtUtil;
import com.example.ecommerce.member.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final MemberMapper memberMapper;
    private final VerificationCodeService verificationCodeService;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final LoginLogProducer loginLogProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    public AuthServiceImpl(MemberMapper memberMapper,
                           VerificationCodeService verificationCodeService,
                           PasswordUtil passwordUtil,
                           JwtUtil jwtUtil,
                           LoginLogProducer loginLogProducer,
                           RedisTemplate<String, Object> redisTemplate) {
        this.memberMapper = memberMapper;
        this.verificationCodeService = verificationCodeService;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
        this.loginLogProducer = loginLogProducer;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse register(RegisterRequest request) {
        String phone = request.getPhone();
        String password = request.getPassword();
        String verificationCode = request.getVerificationCode();
        String nickname = request.getNickname();

        // 1. 验证手机号格式
        if (!passwordUtil.isValidPhone(phone)) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), "手机号格式不正确");
        }

        // 2. 验证密码强度
        if (!passwordUtil.isStrongPassword(password)) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), "密码必须包含字母和数字,长度8-20位");
        }

        // 3. 检查手机号是否已注册
        Member existingMember = memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getPhone, phone)
        );

        if (existingMember != null) {
            throw new BusinessException(ResponseCode.PHONE_ALREADY_EXISTS);
        }

        // 4. 验证验证码
        if (!verificationCodeService.verifyCode(phone, "REGISTER", verificationCode)) {
            throw new BusinessException(ResponseCode.VERIFICATION_CODE_ERROR);
        }

        // 5. 加密密码
        String encodedPassword = passwordUtil.encode(password);

        // 6. 创建会员记录
        Member member = new Member();
        member.setPhone(phone);
        member.setPassword(encodedPassword);
        member.setNickname(nickname != null ? nickname : phone.substring(0, 3) + "****" + phone.substring(7));
        member.setRegisterType("PHONE");
        member.setRegisterTime(LocalDateTime.now());
        member.setAccountStatus(1); // 正常状态

        int rows = memberMapper.insert(member);

        if (rows == 0) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        // 7. 生成JWT Token
        String token = jwtUtil.generateToken(member.getId(), phone);

        // 8. 构建响应
        RegisterResponse response = new RegisterResponse();
        response.setMemberId(member.getId());
        response.setPhone(phone);
        response.setNickname(member.getNickname());
        response.setRegisterTime(member.getRegisterTime());
        response.setToken(token);
        response.setExpiresIn(jwtExpiration);

        log.info("会员注册成功: memberId={}, phone={}", member.getId(), phone);

        // 9. 异步发送注册/登录日志到RocketMQ
        Map<String, Object> logMessage = new HashMap<>();
        logMessage.put("memberId", member.getId());
        logMessage.put("phone", phone);
        logMessage.put("loginType", "REGISTER");
        logMessage.put("loginTime", LocalDateTime.now());
        logMessage.put("loginStatus", 1); // 成功

        loginLogProducer.sendLoginLogAsync("login-log-topic", logMessage);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse thirdPartyRegister(ThirdPartyRegisterRequest request) {
        // TODO: 实现第三方注册逻辑
        // 1. 查询第三方账号是否已绑定
        // 2. 如果未绑定,创建新会员
        // 3. 如果已绑定,返回错误
        // 4. 生成JWT Token

        throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), "第三方注册功能待实现");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse passwordLogin(PasswordLoginRequest request) {
        String phone = request.getPhone();
        String password = request.getPassword();

        // 1. 验证手机号格式
        if (!passwordUtil.isValidPhone(phone)) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), "手机号格式不正确");
        }

        // 2. 查询会员信息
        Member member = memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getPhone, phone)
        );

        if (member == null) {
            throw new BusinessException(ResponseCode.PHONE_NOT_REGISTERED);
        }

        // 3. 验证账号状态
        if (member.getAccountStatus() != 1) {
            if (member.getAccountStatus() == 2) {
                throw new BusinessException(ResponseCode.ACCOUNT_LOCKED);
            } else if (member.getAccountStatus() == 3) {
                throw new BusinessException(ResponseCode.ACCOUNT_NOT_ACTIVATED);
            } else if (member.getAccountStatus() == 4) {
                throw new BusinessException(ResponseCode.ACCOUNT_CANCELED);
            }
        }

        // 4. 验证密码
        if (!passwordUtil.matches(password, member.getPassword())) {
            // TODO: 记录登录失败次数,达到阈值锁定账号
            log.warn("密码错误: phone={}", phone);
            throw new BusinessException(ResponseCode.PASSWORD_ERROR);
        }

        // 5. 更新最后登录时间
        member.setLastLoginTime(LocalDateTime.now());
        memberMapper.updateById(member);

        // 6. 生成JWT Token
        String token = jwtUtil.generateToken(member.getId(), phone);

        // 7. 缓存Token到Redis(用于Token管理和登出)
        String tokenKey = "auth:token:" + member.getId();
        redisTemplate.opsForValue().set(tokenKey, token, jwtExpiration, TimeUnit.SECONDS);

        // 8. 构建响应
        LoginResponse response = new LoginResponse();
        response.setMemberId(member.getId());
        response.setPhone(member.getPhone());
        response.setNickname(member.getNickname());
        response.setAvatar(member.getAvatar());
        response.setAccountStatus(member.getAccountStatus());
        response.setLastLoginTime(member.getLastLoginTime());
        response.setToken(token);
        response.setExpiresIn(jwtExpiration);

        log.info("密码登录成功: memberId={}, phone={}", member.getId(), phone);

        // 9. 异步发送登录日志到RocketMQ
        Map<String, Object> logMessage = new HashMap<>();
        logMessage.put("memberId", member.getId());
        logMessage.put("phone", phone);
        logMessage.put("loginType", "PASSWORD");
        logMessage.put("loginTime", LocalDateTime.now());
        logMessage.put("loginStatus", 1); // 成功

        loginLogProducer.sendLoginLogAsync("login-log-topic", logMessage);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse smsLogin(SmsLoginRequest request) {
        String phone = request.getPhone();
        String verificationCode = request.getVerificationCode();

        // 1. 验证手机号格式
        if (!passwordUtil.isValidPhone(phone)) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), "手机号格式不正确");
        }

        // 2. 验证验证码
        if (!verificationCodeService.verifyCode(phone, "LOGIN", verificationCode)) {
            throw new BusinessException(ResponseCode.VERIFICATION_CODE_ERROR);
        }

        // 3. 查询会员信息
        Member member = memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getPhone, phone)
        );

        if (member == null) {
            throw new BusinessException(ResponseCode.PHONE_NOT_REGISTERED);
        }

        // 4. 验证账号状态
        if (member.getAccountStatus() != 1) {
            if (member.getAccountStatus() == 2) {
                throw new BusinessException(ResponseCode.ACCOUNT_LOCKED);
            } else if (member.getAccountStatus() == 3) {
                throw new BusinessException(ResponseCode.ACCOUNT_NOT_ACTIVATED);
            } else if (member.getAccountStatus() == 4) {
                throw new BusinessException(ResponseCode.ACCOUNT_CANCELED);
            }
        }

        // 5. 更新最后登录时间
        member.setLastLoginTime(LocalDateTime.now());
        memberMapper.updateById(member);

        // 6. 生成JWT Token
        String token = jwtUtil.generateToken(member.getId(), phone);

        // 7. 缓存Token到Redis
        String tokenKey = "auth:token:" + member.getId();
        redisTemplate.opsForValue().set(tokenKey, token, jwtExpiration, TimeUnit.SECONDS);

        // 8. 构建响应
        LoginResponse response = new LoginResponse();
        response.setMemberId(member.getId());
        response.setPhone(member.getPhone());
        response.setNickname(member.getNickname());
        response.setAvatar(member.getAvatar());
        response.setAccountStatus(member.getAccountStatus());
        response.setLastLoginTime(member.getLastLoginTime());
        response.setToken(token);
        response.setExpiresIn(jwtExpiration);

        log.info("验证码登录成功: memberId={}, phone={}", member.getId(), phone);

        // 9. 异步发送登录日志到RocketMQ
        Map<String, Object> logMessage = new HashMap<>();
        logMessage.put("memberId", member.getId());
        logMessage.put("phone", phone);
        logMessage.put("loginType", "SMS");
        logMessage.put("loginTime", LocalDateTime.now());
        logMessage.put("loginStatus", 1); // 成功

        loginLogProducer.sendLoginLogAsync("login-log-topic", logMessage);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordRequest request) {
        String phone = request.getPhone();
        String verificationCode = request.getVerificationCode();
        String newPassword = request.getNewPassword();

        // 1. 验证手机号格式
        if (!passwordUtil.isValidPhone(phone)) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), "手机号格式不正确");
        }

        // 2. 验证新密码强度
        if (!passwordUtil.isStrongPassword(newPassword)) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), "密码必须包含字母和数字,长度8-20位");
        }

        // 3. 验证验证码
        if (!verificationCodeService.verifyCode(phone, "RESET_PASSWORD", verificationCode)) {
            throw new BusinessException(ResponseCode.VERIFICATION_CODE_ERROR);
        }

        // 4. 查询会员信息
        Member member = memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getPhone, phone)
        );

        if (member == null) {
            throw new BusinessException(ResponseCode.PHONE_NOT_REGISTERED);
        }

        // 5. 验证新密码不能与旧密码相同(可选)
        if (passwordUtil.matches(newPassword, member.getPassword())) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), "新密码不能与旧密码相同");
        }

        // 6. 加密新密码
        String encodedPassword = passwordUtil.encode(newPassword);
        member.setPassword(encodedPassword);

        // 7. 更新密码
        int rows = memberMapper.updateById(member);

        if (rows == 0) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        // 8. 清除该会员的所有Token(强制重新登录)
        String tokenKey = "auth:token:" + member.getId();
        redisTemplate.delete(tokenKey);

        log.info("密码重置成功: memberId={}, phone={}", member.getId(), phone);
    }

    @Override
    public void logout(String token) {
        try {
            // 从Token中获取会员ID
            Long memberId = jwtUtil.getMemberIdFromToken(token);

            // 删除Redis中的Token
            String tokenKey = "auth:token:" + memberId;
            redisTemplate.delete(tokenKey);

            // TODO: 可以将Token加入黑名单(使用Redis Set),防止重用
            // String blacklistKey = "auth:blacklist:" + token;
            // redisTemplate.opsForValue().set(blacklistKey, "1", jwtExpiration, TimeUnit.SECONDS);

            log.info("会员登出成功: memberId={}", memberId);

        } catch (Exception e) {
            log.error("登出失败", e);
            // 登出失败不抛出异常,避免影响用户体验
        }
    }
}
