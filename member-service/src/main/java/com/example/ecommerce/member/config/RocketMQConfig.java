package com.example.ecommerce.member.config;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ 配置类
 * 用于配置消息生产者和消费者
 */
@Configuration
public class RocketMQConfig {

    // RocketMQ 基础配置已在 application.yml 中配置
    // 这里仅作为配置类的占位符
    // 具体的 Producer 和 Consumer 在各自的类中使用 @RocketMQMessageListener 注解配置

    /**
     * 事务监听器示例(如需要事务消息可取消注释)
     */
    // @RocketMQTransactionListener
    // public class TransactionListenerImpl implements RocketMQLocalTransactionListener {
    //     @Override
    //     public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
    //         // 执行本地事务
    //         return RocketMQLocalTransactionState.COMMIT;
    //     }
    //
    //     @Override
    //     public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
    //         // 检查本地事务状态
    //         return RocketMQLocalTransactionState.COMMIT;
    //     }
    // }
}
