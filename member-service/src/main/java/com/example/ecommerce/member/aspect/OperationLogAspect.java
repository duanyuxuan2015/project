package com.example.ecommerce.member.aspect;

import com.example.ecommerce.member.annotation.OperationLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * 操作日志切面
 * 用于记录关键操作日志
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    private final ObjectMapper objectMapper;

    public OperationLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 定义切点:所有带 @OperationLog 注解的方法
     */
    @Pointcut("@annotation(com.example.ecommerce.member.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 环绕通知:记录操作日志
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationLog operationLog = signature.getMethod().getAnnotation(OperationLog.class);

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();

        // 设置 MDC(用于日志输出)
        String ip = getClientIp(request);
        MDC.put("ip", ip);

        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;

        try {
            // 执行方法
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录日志
            logOperation(joinPoint, operationLog, request, result, exception, duration);

            // 清理 MDC
            MDC.remove("ip");
            MDC.remove("memberId");
            MDC.remove("phone");
        }
    }

    /**
     * 记录操作日志
     */
    private void logOperation(ProceedingJoinPoint joinPoint, OperationLog operationLog,
                              HttpServletRequest request, Object result, Throwable exception, long duration) {
        try {
            String module = operationLog.module();
            String operationType = operationLog.operationType();
            String description = operationLog.description();

            StringBuilder logMessage = new StringBuilder();
            logMessage.append("【操作日志】");
            logMessage.append("模块:").append(module).append(", ");
            logMessage.append("操作:").append(operationType).append(", ");
            logMessage.append("描述:").append(description).append(", ");
            logMessage.append("URI:").append(request.getRequestURI()).append(", ");
            logMessage.append("耗时:").append(duration).append("ms");

            // 记录请求参数
            if (operationLog.logRequest()) {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    String params = objectMapper.writeValueAsString(args);
                    logMessage.append(", 参数:").append(params);
                }
            }

            // 记录响应结果
            if (operationLog.logResponse() && result != null) {
                String response = objectMapper.writeValueAsString(result);
                logMessage.append(", 响应:").append(response);
            }

            // 根据执行结果选择日志级别
            if (exception != null) {
                if (operationLog.logException()) {
                    log.error(logMessage.toString(), exception);
                } else {
                    log.error(logMessage.toString());
                }
            } else {
                log.info(logMessage.toString());
            }
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
