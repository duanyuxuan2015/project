package com.example.ecommerce.member.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作模块
     *
     * @return 模块名称
     */
    String module() default "";

    /**
     * 操作类型
     * 例如: REGISTER, LOGIN, UPDATE_INFO, etc.
     *
     * @return 操作类型
     */
    String operationType() default "";

    /**
     * 操作描述
     *
     * @return 描述
     */
    String description() default "";

    /**
     * 是否记录请求参数
     *
     * @return true:记录, false:不记录
     */
    boolean logRequest() default true;

    /**
     * 是否记录响应结果
     *
     * @return true:记录, false:不记录
     */
    boolean logResponse() default false;

    /**
     * 是否记录异常堆栈
     *
     * @return true:记录, false:不记录
     */
    boolean logException() default true;
}
