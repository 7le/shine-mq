package top.arkstack.shine.mq.processor;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

/**
 * 消费接口
 *
 * @author 7le
 * @version 1.0.0
 */
public interface Processor {

    Object process(Object msg, Message message, Channel channel);
}
