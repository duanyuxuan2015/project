package com.example.ecommerce.member.enums;

import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
public enum ResponseCode {

    // 成功
    SUCCESS("0000", "成功"),

    // 客户端错误 4xxx
    BAD_REQUEST("4000", "请求参数错误"),
    UNAUTHORIZED("4001", "未授权,请先登录"),
    FORBIDDEN("4003", "无权限访问"),
    NOT_FOUND("4004", "资源不存在"),

    // 业务错误 5xxx
    PHONE_ALREADY_EXISTS("5001", "手机号已注册"),
    PHONE_NOT_REGISTERED("5002", "手机号未注册"),
    PASSWORD_ERROR("5003", "密码错误"),
    VERIFICATION_CODE_ERROR("5004", "验证码错误或已过期"),
    VERIFICATION_CODE_SEND_TOO_FREQUENT("5005", "验证码发送过于频繁"),
    ACCOUNT_LOCKED("5006", "账号已被锁定"),
    ACCOUNT_NOT_ACTIVATED("5007", "账号未激活"),
    ACCOUNT_CANCELED("5008", "账号已注销"),
    THIRD_PARTY_BINDING_EXISTS("5009", "第三方账号已绑定其他会员"),
    THIRD_PARTY_BINDING_NOT_FOUND("5010", "未找到第三方账号绑定"),

    // 服务器错误 9xxx
    INTERNAL_SERVER_ERROR("9000", "服务器内部错误"),
    DATABASE_ERROR("9001", "数据库操作失败"),
    REDIS_ERROR("9002", "缓存服务异常"),
    SMS_SEND_FAILED("9003", "短信发送失败"),
    FILE_UPLOAD_FAILED("9004", "文件上传失败");

    /**
     * 响应码
     */
    private final String code;

    /**
     * 响应信息
     */
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
