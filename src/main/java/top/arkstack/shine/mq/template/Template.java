package top.arkstack.shine.mq.template;


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
    void send(String queueName, String exchangeName, Object msg, String routingKey, int expiration, int priority) throws Exception;

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
}
