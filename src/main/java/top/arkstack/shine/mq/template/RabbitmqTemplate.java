package top.arkstack.shine.mq.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import top.arkstack.shine.mq.RabbitmqFactory;
import top.arkstack.shine.mq.bean.EventMessage;
import top.arkstack.shine.mq.bean.SendTypeEnum;

import java.util.Objects;
import java.util.UUID;

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

    private RabbitmqFactory rabbitmqFactory;

    public RabbitmqTemplate(RabbitmqFactory rabbitmqFactory, AmqpTemplate amqpTemplate,
                            MessageConverter messageConverter) {
        this.rabbitmqFactory = rabbitmqFactory;
        this.eventAmqpTemplate = amqpTemplate;
        this.messageConverter = messageConverter;
    }


    @Override
    public void send(String exchangeName, Object msg, String routingKey) throws Exception {
        this.send(exchangeName, msg, messageConverter, SendTypeEnum.DIRECT, routingKey, 0, 0);
    }

    @Override
    public void send(String exchangeName, Object msg, String routingKey, int expiration) throws Exception {
        this.send(exchangeName, msg, messageConverter, SendTypeEnum.DIRECT, routingKey, expiration, 0);
    }

    @Override
    public void send(String exchangeName, Object msg, String routingKey, int expiration, int priority) throws Exception {
        this.send(exchangeName, msg, messageConverter, SendTypeEnum.DIRECT, routingKey, expiration, priority);
    }

    @Override
    public void send(String exchangeName, Object msg, String routingKey, int expiration, int priority,
                     SendTypeEnum type) throws Exception {
        this.send(exchangeName, msg, messageConverter, type, routingKey, expiration, priority);
    }

    @Override
    public void send(EventMessage message, int expiration, int priority, SendTypeEnum type) throws Exception {
        this.sendWithEM(message, expiration, priority, type);
    }

    private Object send(String exchangeName, Object msg, MessageConverter messageConverter, SendTypeEnum type,
                        String routingKey, int expiration, int priority) throws Exception {
        check(exchangeName, routingKey);

        Object obj = null;
        String msgId = UUID.randomUUID().toString();
        EventMessage eventMessage = new EventMessage(exchangeName, routingKey, type.toString(), msg, null, msgId);
        MessageProperties messageProperties = new MessageProperties();
        //过期时间
        if (expiration > 0) {
            messageProperties.setExpiration(String.valueOf(expiration));
        }
        //消息优先级
        if (priority > 0) {
            messageProperties.setPriority(priority);
        }
        messageProperties.setMessageId(msgId);
        // 设置消息持久化
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        Message message = messageConverter.toMessage(eventMessage, messageProperties);
        rabbitmqFactory.setCorrelationData(msgId, null, eventMessage, null);
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

    private Object sendWithEM(EventMessage eventMessage, int expiration, int priority, SendTypeEnum type) throws Exception {

        Object obj = null;
        MessageProperties messageProperties = new MessageProperties();
        //过期时间
        if (expiration > 0) {
            messageProperties.setExpiration(String.valueOf(expiration));
        }
        //消息优先级
        if (priority > 0) {
            messageProperties.setPriority(priority);
        }
        messageProperties.setMessageId(eventMessage.getMessageId());
        // 设置消息持久化
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        Message message = messageConverter.toMessage(eventMessage, messageProperties);
        rabbitmqFactory.setCorrelationData(eventMessage.getMessageId(), eventMessage.getCoordinator(), eventMessage, null);
        try {
            if (SendTypeEnum.RPC.equals(type)) {
                obj = eventAmqpTemplate.convertSendAndReceive(eventMessage.getRoutingKey(), message);
            } else {
                eventAmqpTemplate.send(eventMessage.getExchangeName(), eventMessage.getRoutingKey(), message);
            }
        } catch (AmqpException e) {
            logger.error("send event fail. Event Message : [{}]", eventMessage, e);
            throw new Exception("send event fail", e);
        }
        return obj;
    }

    private void check(String exchangeName, String routingKey) {
        Objects.requireNonNull(exchangeName, "The exchangeName is empty.");
        Objects.requireNonNull(routingKey, "The routingKey is empty.");
        Objects.requireNonNull(messageConverter, "The messageConverter is empty.");
    }
}
