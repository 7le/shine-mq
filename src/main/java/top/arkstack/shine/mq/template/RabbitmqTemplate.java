package top.arkstack.shine.mq.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import top.arkstack.shine.mq.bean.EventMessage;
import top.arkstack.shine.mq.bean.SendTypeEnum;

import java.util.Objects;

/**
 * 封装AMQP的一些操作
 *
 * @author 7le
 * @version 1.0.0
 */
public class RabbitmqTemplate implements Template {

    private static final Logger logger = LoggerFactory.getLogger(RabbitmqTemplate.class);

    private AmqpTemplate eventAmqpTemplate;

    private MessageConverter messageConverter;

    public RabbitmqTemplate(AmqpTemplate eopAmqpTemplate, MessageConverter messageConverter) {
        this.eventAmqpTemplate = eopAmqpTemplate;
        this.messageConverter = messageConverter;
    }


    @Override
    public void send(String queueName, String exchangeName, Object msg, String routingKey) throws Exception {
        this.send(queueName, exchangeName, msg, messageConverter, SendTypeEnum.DIRECT, routingKey, 0, 0);
    }

    @Override
    public void send(String queueName, String exchangeName, Object msg, String routingKey,
                     int expiration) throws Exception {
        this.send(queueName, exchangeName, msg, messageConverter, SendTypeEnum.DIRECT, routingKey, expiration, 0);
    }

    @Override
    public void send(String queueName, String exchangeName, Object msg, String routingKey,
                     int expiration, int priority) throws Exception {
        this.send(queueName, exchangeName, msg, messageConverter, SendTypeEnum.DIRECT, routingKey, expiration, priority);
    }

    @Override
    @Deprecated
    public Object sendAndReceive(String queueName, String exchangeName, Object msg) throws Exception {
        return this.send(queueName, exchangeName, msg, messageConverter, SendTypeEnum.RPC, queueName, 0, 0);
    }


    private Object send(String queueName, String exchangeName, Object msg,
                        MessageConverter messageConverter, SendTypeEnum type, String routingKey, int expiration, int priority) throws Exception {
        Objects.requireNonNull(queueName, "The queueName is empty.");
        Objects.requireNonNull(exchangeName, "The exchangeName is empty.");
        Objects.requireNonNull(routingKey, "The routingKey is empty.");
        Objects.requireNonNull(messageConverter, "The messageConverter is empty.");

        Object obj = null;
        EventMessage eventMessage = new EventMessage(queueName, exchangeName, routingKey, msg);
        MessageProperties messageProperties = new MessageProperties();
        //过期时间
        if (expiration > 0) {
            messageProperties.setExpiration(String.valueOf(expiration));
        }
        //消息优先级
        if (priority > 0) {
            messageProperties.setPriority(priority);
        }
        Message message = messageConverter.toMessage(eventMessage, messageProperties);
        try {
            if (SendTypeEnum.RPC.equals(type)) {
                obj = eventAmqpTemplate.convertSendAndReceive(routingKey, message);
            } else {
                eventAmqpTemplate.send(exchangeName, routingKey, message);
            }
        } catch (AmqpException e) {
            logger.error("send event fail. Event Message : [{}]", eventMessage, e);
            throw new Exception("send event fail", e);
        }
        return obj;
    }
}
