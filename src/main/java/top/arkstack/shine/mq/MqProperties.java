package top.arkstack.shine.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息队列的基础配置信息
 *
 * @author 7le
 * @version 1.1.0
 */
@Data
@ConfigurationProperties("shine.mq")
public class MqProperties {

    private final Distributed distributed = new Distributed();

    private final Rabbit rabbit = new Rabbit();

    @Data
    public static class Distributed {

        /**
         * 是否初始化 开启分布式事务 缺省为false
         */
        private boolean transaction = false;

        /**
         * 提交ack 失败最大重试次数
         */
        private Integer maxRetries = 3;
    }

    @Data
    public static class Rabbit {

        /**
         * {@link org.springframework.amqp.core.AcknowledgeMode}
         * <p>
         * 0 AUTO
         * 1 MANUAL
         * 2 NONE
         */
        private int acknowledgeMode = 1;

        /**
         * 每个消费者可能未完成的未确认消息的数量。
         */
        private Integer prefetchCount = null;

        /**
         * 为每个已配置队列创建的消费者数
         */
        private Integer consumersPerQueue = null;

        /**
         * 是否持久化，指是否保存到erlang自带得数据库mnesia中，即重启服务是否消失
         */
        private boolean durable = true;

        /**
         * 是否排外，指当前定义的队列是connection中的channel共享的，其他connection连接访问不到
         */
        private boolean exclusive = false;

        /**
         * 是否自动删除，指当connection.close时队列删除
         */
        private boolean autoDelete = false;

        /**
         * 是否初始化消息监听者， 若服务只是Producer则关闭
         */
        private boolean listenerEnable = false;

        /**
         * 通道缓存
         */
        private Integer channelCacheSize = null;
    }
}
