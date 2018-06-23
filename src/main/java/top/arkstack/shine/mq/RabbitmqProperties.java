package top.arkstack.shine.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息队列的配置信息
 *
 * @author 7le
 * @version 1.0.0
 */
@Data
@ConfigurationProperties("shine.mq.rabbit")
public class RabbitmqProperties {

    /**
     * ip
     */
    private String host = "127.0.0.1";

    /**
     * 端口号
     */
    private int port = 5672;
    /**
     * 账号
     */
    private String username = "guest";
    /**
     * 密码
     */
    private String password = "guest";

    /**
     * 允许空闲的最大通道数
     */
    private int channelCacheSize = 25;

    /**
     * 建立连接的超时时间
     */
    private int connectionTimeout = 0;

    /**
     * 每次从队列中取几条，只有等收到Ack了才重新取
     * 0为循环调度 1为公平调度
     */
    private int prefetchSize = 1;

    /**
     * 虚拟主机
     */
    private String virtualHost;

    /**
     * 消费者数量 缺省为CPU核数*2
     */
    private int processSize = Runtime.getRuntime().availableProcessors() << 1;

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
}
