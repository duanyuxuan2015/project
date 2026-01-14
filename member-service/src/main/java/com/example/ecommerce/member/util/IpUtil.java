package com.example.ecommerce.member.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP地址工具类
 * 用于获取客户端IP地址和解析IP地域
 */
public class IpUtil {

    /**
     * 获取客户端真实IP地址
     * 考虑代理、负载均衡等场景
     *
     * @param request HTTP请求
     * @return IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个IP的情况(通过逗号分隔)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // 处理localhost IPv6地址
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    /**
     * 验证IP地址格式是否正确
     *
     * @param ip IP地址
     * @return true:格式正确, false:格式错误
     */
    public static boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        // IPv4地址格式验证
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        if (ip.matches(ipv4Pattern)) {
            return true;
        }

        // IPv6地址格式验证(简化版)
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
        return ip.matches(ipv6Pattern);
    }

    /**
     * 解析IP地址的地域信息
     * TODO: 集成第三方IP地域解析服务
     *
     * @param ip IP地址
     * @return 地域信息(如"北京市朝阳区")
     */
    public static String getIpRegion(String ip) {
        if (!isValidIp(ip)) {
            return "未知";
        }

        // TODO: 调用第三方IP解析服务
        // 方案1: 使用淘宝IP地址库(免费)
        // 方案2: 使用百度IP定位API
        // 方案3: 使用IP2Region库(本地离线解析)
        // 方案4: 使用GeoIP2库

        // 示例伪代码:
        // String region = ip2region.search(ip);
        // return region;

        return "未知";
    }

    /**
     * 判断是否为内网IP
     *
     * @param ip IP地址
     * @return true:内网IP, false:外网IP
     */
    public static boolean isInternalIp(String ip) {
        if (!isValidIp(ip)) {
            return false;
        }

        // 127.0.0.1
        if ("127.0.0.1".equals(ip)) {
            return true;
        }

        // 10.0.0.0 - 10.255.255.255
        if (ip.startsWith("10.")) {
            return true;
        }

        // 172.16.0.0 - 172.31.255.255
        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length >= 2) {
                int secondOctet = Integer.parseInt(parts[1]);
                return secondOctet >= 16 && secondOctet <= 31;
            }
        }

        // 192.168.0.0 - 192.168.255.255
        if (ip.startsWith("192.168.")) {
            return true;
        }

        return false;
    }

    /**
     * 获取设备类型
     * 根据User-Agent判断设备类型
     *
     * @param userAgent User-Agent字符串
     * @return 设备类型(iOS/Android/PC/Unknown)
     */
    public static String getDeviceType(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }

        String ua = userAgent.toLowerCase();

        // iOS设备
        if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ipod")) {
            return "iOS";
        }

        // Android设备
        if (ua.contains("android")) {
            return "Android";
        }

        // PC设备
        if (ua.contains("windows") || ua.contains("macintosh") || ua.contains("linux")) {
            return "PC";
        }

        return "Unknown";
    }
}
