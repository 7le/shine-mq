package top.arkstack.shine.mq.template;


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
     * @param queueName    队列名
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @throws Exception
     */
    void send(String queueName, String exchangeName, Object msg, String routingKey) throws Exception;


    /**
     * 带路由密钥的消息发送，对消息进行过期设置
     *
     * @param queueName    队列名
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @param expiration   过期时间
     * @throws Exception
     */
    void send(String queueName, String exchangeName, Object msg, String routingKey, int expiration) throws Exception;

    /**
     * 带路由密钥的消息发送，对消息进行过期设置，并设置优先级
     *
     * @param queueName    队列名
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @param expiration   过期时间
     * @param priority     优先级
     * @throws Exception
     */
    void send(String queueName, String exchangeName, Object msg, String routingKey,
              int expiration, int priority) throws Exception;

    /**
     * 带路由密钥的消息发送，对消息进行过期设置，并设置优先级
     * 可选择对应exchange模式
     *
     * @param queueName    队列名
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @param expiration   过期时间
     * @param priority     优先级
     * @throws Exception
     */
    void send(String queueName, String exchangeName, Object msg, String routingKey, int expiration,
              int priority, SendTypeEnum type) throws Exception;

    /**
     * rpc(远程调用) 待完善
     *
     * @param queueName    队列名
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @return
     * @throws Exception
     */
    Object sendAndReceive(String queueName, String exchangeName, Object msg) throws Exception;

    /**
     * 带路由密钥的消息发送
     * (直接发送 不会包装为 {@link top.arkstack.shine.mq.bean.EventMessage} 不过也不能直接使用xpush接收)
     *
     * @param queueName    队列名
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @throws Exception
     */
    void sendSimple(String queueName, String exchangeName, Object msg, String routingKey) throws Exception;

    /**
     * 带路由密钥的消息发送，对消息进行过期设置
     * (直接发送 不会包装为 {@link top.arkstack.shine.mq.bean.EventMessage} 不过也不能直接使用xpush接收)
     *
     * @param queueName    队列名
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @param expiration   过期时间
     * @throws Exception
     */
    void sendSimple(String queueName, String exchangeName, Object msg, String routingKey, int expiration) throws Exception;

    /**
     * 带路由密钥的消息发送，对消息进行过期设置，并设置优先级
     * (直接发送 不会包装为 {@link top.arkstack.shine.mq.bean.EventMessage} 不过也不能直接使用xpush接收)
     *
     * @param queueName    队列名
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @param expiration   过期时间
     * @param priority     优先级
     * @throws Exception
     */
    void sendSimple(String queueName, String exchangeName, Object msg, String routingKey, int expiration,
                    int priority) throws Exception;

    /**
     * 带路由密钥的消息发送，对消息进行过期设置，并设置优先级  可选择对应exchange模式
     * (直接发送 不会包装为 {@link top.arkstack.shine.mq.bean.EventMessage} 不过也不能直接使用xpush接收)
     *
     * @param queueName    队列名
     * @param exchangeName 交换器
     * @param msg          发送对象
     * @param routingKey   路由密钥
     * @param expiration   过期时间
     * @param priority     优先级
     * @throws Exception
     */
    void sendSimple(String queueName, String exchangeName, Object msg, String routingKey, int expiration,
                    int priority, SendTypeEnum type) throws Exception;
}
