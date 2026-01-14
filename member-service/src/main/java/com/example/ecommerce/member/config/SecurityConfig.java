package com.example.ecommerce.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 * 配置JWT认证和授权规则
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码编码器
     * 使用 BCrypt 算法加密密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 过滤链配置
     * 配置接口访问权限和JWT过滤器
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF(使用JWT不需要CSRF保护)
            .csrf(AbstractHttpConfigurer::disable)

            // 配置会话管理为无状态
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 配置授权规则
            .authorizeHttpRequests(auth -> auth
                // 公开接口: 注册、登录、验证码
                .requestMatchers(
                    "/v1/auth/register",
                    "/v1/auth/login",
                    "/v1/auth/send-code",
                    "/v1/auth/third-party/**"
                ).permitAll()

                // 健康检查接口公开
                .requestMatchers("/v1/actuator/**").permitAll()

                // API文档接口公开
                .requestMatchers("/v1/swagger-ui/**", "/v1/v3/api-docs/**").permitAll()

                // 其他接口需要认证
                .anyRequest().authenticated()
            )

            // 禁用 form 登录
            .formLogin(AbstractHttpConfigurer::disable)

            // 禁用 logout
            .logout(AbstractHttpConfigurer::disable);

        // TODO: 添加 JWT 过滤器
        // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
