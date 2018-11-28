package top.arkstack.shine.mq;

import org.springframework.amqp.support.converter.MessageConverter;
import top.arkstack.shine.mq.bean.SendTypeEnum;
import top.arkstack.shine.mq.processor.Processor;

/**
 * @author 7le
 * @version 1.0.0
 */
public interface Factory {

    /**
     * 添加exchange和queue
     * <BR>
     * exchange模式对应{@link top.arkstack.shine.mq.bean.SendTypeEnum}
     * 默认为 {@link SendTypeEnum#DIRECT} 暂时先支持{@link SendTypeEnum#DIRECT}和{@link SendTypeEnum#TOPIC}
     * <BR>
     * 仅为生产者
     *
     * @param queueName    队列
     * @param exchangeName 交换器
     * @param routingKey   路由密钥
     * @return
     */
    Factory add(String queueName, String exchangeName, String routingKey);

    /**
     * 添加exchange和queue
     * <BR>
     * exchange模式对应{@link top.arkstack.shine.mq.bean.SendTypeEnum}
     * 默认为 {@link SendTypeEnum#DIRECT} 暂时先支持{@link SendTypeEnum#DIRECT}和{@link SendTypeEnum#TOPIC}
     * <BR>
     * 仅为生产者
     *
     * @param queueName    队列
     * @param exchangeName 交换器
     * @param routingKey   路由密钥
     * @param type         exchange模式
     * @return
     */
    Factory add(String queueName, String exchangeName, String routingKey, SendTypeEnum type);

    /**
     * 添加exchange和queue
     * <BR>
     * exchange模式对应{@link top.arkstack.shine.mq.bean.SendTypeEnum}
     * 默认为 {@link SendTypeEnum#DIRECT}
     * <BR>
     * 生产者创建队列不需要增加processor，消费者需要添加processor
     *
     * @param queueName    队列
     * @param exchangeName 交换器
     * @param routingKey   路由密钥
     * @param processor    处理器
     * @return
     */
    Factory add(String queueName, String exchangeName, String routingKey, Processor processor);

    /**
     * 添加exchange和queue
     * <BR>
     * exchange模式对应{@link top.arkstack.shine.mq.bean.SendTypeEnum}
     * 默认为 {@link SendTypeEnum#DIRECT} 暂时先支持{@link SendTypeEnum#DIRECT}和{@link SendTypeEnum#TOPIC}
     * <BR>
     * 生产者创建队列不需要增加processor，消费者需要添加processor
     *
     * @param queueName    队列
     * @param exchangeName 交换器
     * @param routingKey   路由密钥
     * @param processor    处理器
     * @param type         exchange模式
     * @return
     */
    Factory add(String queueName, String exchangeName, String routingKey, Processor processor, SendTypeEnum type);

    /**
     * 添加exchange和queue
     * <BR>
     * exchange模式对应{@link top.arkstack.shine.mq.bean.SendTypeEnum}
     * 默认为 {@link SendTypeEnum#DIRECT} 暂时先支持{@link SendTypeEnum#DIRECT}和{@link SendTypeEnum#TOPIC}
     * <BR>
     * 生产者创建队列不需要增加processor，消费者需要添加processor
     *
     * @param queueName        队列
     * @param exchangeName     交换器
     * @param routingKey       路由密钥
     * @param processor        处理器
     * @param messageConverter 序列化处理
     * @param type             exchange模式
     * @return
     */
    Factory add(String queueName, String exchangeName, String routingKey, Processor processor, SendTypeEnum type, MessageConverter messageConverter);

    /**
     * 添加死信队列
     * <BR>
     * exchange模式对应{@link top.arkstack.shine.mq.bean.SendTypeEnum}
     * 默认为 {@link SendTypeEnum#DIRECT} 暂时先支持{@link SendTypeEnum#DIRECT}和{@link SendTypeEnum#TOPIC}
     * 若为分布式事务则指定{@link SendTypeEnum#DISTRIBUTED}
     * <BR>
     * 生产者创建队列不需要增加processor，消费者需要添加processor
     *
     * @param queueName    队列
     * @param exchangeName 交换器
     * @param routingKey   路由密钥
     * @param processor    处理器
     * @param type         exchange模式
     * @return
     */
    Factory addDLX(String queueName, String exchangeName, String routingKey, Processor processor, SendTypeEnum type);

    /**
     * 添加死信队列
     * <BR>
     * exchange模式对应{@link top.arkstack.shine.mq.bean.SendTypeEnum}
     * 默认为 {@link SendTypeEnum#DIRECT} 暂时先支持{@link SendTypeEnum#DIRECT}和{@link SendTypeEnum#TOPIC}
     * 若为分布式事务则指定{@link SendTypeEnum#DISTRIBUTED}
     * <BR>
     * 生产者创建队列不需要增加processor，消费者需要添加processor
     *
     * @param queueName        队列
     * @param exchangeName     交换器
     * @param routingKey       路由密钥
     * @param processor        处理器
     * @param messageConverter 序列化处理
     * @param type             exchange模式
     * @return
     */
    Factory addDLX(String queueName, String exchangeName, String routingKey, Processor processor, SendTypeEnum type, MessageConverter messageConverter);
}
