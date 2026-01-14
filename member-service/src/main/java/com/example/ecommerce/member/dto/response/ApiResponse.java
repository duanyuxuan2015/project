package com.example.ecommerce.member.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应类
 *
 * @param <T> 数据类型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应(无数据)
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>("0000", "成功");
    }

    /**
     * 成功响应(带数据)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("0000", "成功", data);
    }

    /**
     * 成功响应(自定义消息)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("0000", message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message);
    }

    /**
     * 失败响应(仅消息)
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("9999", message);
    }
}
