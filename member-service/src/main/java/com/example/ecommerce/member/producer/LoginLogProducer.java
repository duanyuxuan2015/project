package com.example.ecommerce.member.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 登录日志消息生产者
 * 用于异步发送登录日志到RocketMQ
 */
@Slf4j
@Component
public class LoginLogProducer {

    private final RocketMQTemplate rocketMQTemplate;

    @Autowired
    public LoginLogProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    /**
     * 发送登录日志消息
     *
     * @param topic   消息主题
     * @param message 消息内容
     */
    public void sendLoginLog(String topic, Object message) {
        try {
            Message<Object> msg = MessageBuilder.withPayload(message).build();
            rocketMQTemplate.syncSend(topic, msg);
            log.info("登录日志发送成功: topic={}, messageId={}", topic, message);
        } catch (Exception e) {
            log.error("登录日志发送失败: topic={}, message={}", topic, message, e);
            // 发送失败不影响主业务,仅记录日志
        }
    }

    /**
     * 异步发送登录日志消息
     *
     * @param topic   消息主题
     * @param message 消息内容
     */
    public void sendLoginLogAsync(String topic, Object message) {
        try {
            rocketMQTemplate.asyncSend(topic, message, new DefaultSendCallback(), 3000);
            log.debug("登录日志异步发送: topic={}", topic);
        } catch (Exception e) {
            log.error("登录日志异步发送失败: topic={}, message={}", topic, message, e);
        }
    }

    /**
     * 默认发送回调
     */
    private static class DefaultSendCallback implements org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener {
        // 这里可以添加成功/失败的回调处理
    }
}
