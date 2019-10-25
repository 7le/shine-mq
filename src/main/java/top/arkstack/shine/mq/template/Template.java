package top.arkstack.shine.mq.template;


import top.arkstack.shine.mq.bean.EventMessage;
import top.arkstack.shine.mq.bean.SendTypeEnum;

/**
 * 封装一些操作
 *
 * @author 7le
 * @version 1.0.0
 */
public interface Template {

    /**
     * 带路由密钥的消息发送
     *
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @throws Exception
     */
    void send(String exchangeName, Object msg, String routingKey) throws Exception;


    /**
     * 带路由密钥的消息发送，对消息进行过期设置
     *
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @param expiration   过期时间
     * @throws Exception
     */
    void send(String exchangeName, Object msg, String routingKey, int expiration) throws Exception;

    /**
     * 带路由密钥的消息发送，对消息进行过期设置，并设置优先级
     *
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @param expiration   过期时间
     * @param priority     优先级
     * @throws Exception
     */
    void send(String exchangeName, Object msg, String routingKey,
              int expiration, int priority) throws Exception;

    /**
     * 带路由密钥的消息发送，对消息进行过期设置，并设置优先级
     * 可选择对应exchange模式
     *
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @param expiration   过期时间
     * @param priority     优先级
     * @param type         消息类型
     * @throws Exception
     */
    void send(String exchangeName, Object msg, String routingKey, int expiration,
              int priority, SendTypeEnum type) throws Exception;

    /**
     * 发送封装好的消息
     *
     * @param message      包装好的消息
     * @param expiration   过期时间
     * @param priority     优先级
     * @param type         消息类型
     * @throws Exception
     */
    void send(EventMessage message, int expiration, int priority, SendTypeEnum type) throws Exception;
}
