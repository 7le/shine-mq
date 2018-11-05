package top.arkstack.shine.mq;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import top.arkstack.shine.mq.bean.EventMessage;
import top.arkstack.shine.mq.bean.SendTypeEnum;
import top.arkstack.shine.mq.constant.MqConstant;
import top.arkstack.shine.mq.coordinator.Coordinator;
import top.arkstack.shine.mq.processor.Processor;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 消息适配处理器
 *
 * @author 7le
 * @version 1.0.0
 */
@Slf4j
@Component
public class MessageAdapterHandler implements ChannelAwareMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageAdapterHandler.class);

    private ConcurrentMap<String, ProcessorWrap> map;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    RabbitmqFactory rabbitmqFactory;

    protected MessageAdapterHandler() {
        this.map = new ConcurrentHashMap<>();
    }

    protected void add(String exchangeName, String routingKey, Processor processor, SendTypeEnum type,
                       MessageConverter messageConverter) {

        Objects.requireNonNull(exchangeName, "The exchangeName is empty.");
        Objects.requireNonNull(messageConverter, "The messageConverter is empty.");
        Objects.requireNonNull(routingKey, "The routingKey is empty.");

        ProcessorWrap pw = new ProcessorWrap(messageConverter, processor);
        ProcessorWrap oldProcessorWrap = map.putIfAbsent(exchangeName + "_" + routingKey + "_" +
                (type == null ? SendTypeEnum.DIRECT.toString() : type.toString()), pw);
        if (oldProcessorWrap != null) {
            logger.warn("The processor of this queue and exchange exists");
        }
    }

    @Override
    public void onMessage(Message message, Channel channel) {
        EventMessage em;
        String msgId = message.getMessageProperties().getMessageId();
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            em = JSON.parseObject(message.getBody(), EventMessage.class);
            ProcessorWrap wrap = map.get(em.getExchangeName() + "_" + em.getRoutingKey() + "_" + em.getSendTypeEnum());
            wrap.process(em.getData(), message, channel);
            Coordinator coordinator = null;
            try {
                //如果是分布式事务的消息，sdk提供ack应答，无须自己手动ack
                if (SendTypeEnum.DISTRIBUTED.toString().equals(em.getSendTypeEnum())) {
                    Objects.requireNonNull(em.getCoordinator(),
                            "Distributed transaction message error: coordinator is null.");
                    coordinator = (Coordinator) applicationContext.getBean(em.getCoordinator());
                    channel.basicAck(tag, false);
                }
            } catch (IOException e) {
                log.error("Consume message failed , message: {} :", message.getBody(), e);
                if (SendTypeEnum.DISTRIBUTED.toString().equals(em.getSendTypeEnum())) {
                    Double resendCount = coordinator.incrementResendKey(MqConstant.RECEIVE_RETRIES, msgId);
                    if (resendCount >= rabbitmqFactory.getConfig().getDistributed().getReceiveMaxRetries()) {
                        // 放入死信队列
                        channel.basicNack(tag, false, false);
                        coordinator.delResendKey(MqConstant.RECEIVE_RETRIES, msgId);
                    } else {
                        // 重新放入队列 等待消费
                        channel.basicNack(tag, false, true);
                    }
                }
            }
        } catch (Exception e) {
            log.error("MessageAdapterHandler error, message: {} :", message.getBody(), e);
        }
    }


    protected Set<String> getAllBinding() {
        Set<String> keySet = map.keySet();
        return keySet;
    }

    protected static class ProcessorWrap {

        private MessageConverter messageConverter;

        private Processor processor;

        protected ProcessorWrap(MessageConverter messageConverter, Processor processor) {
            this.messageConverter = messageConverter;
            this.processor = processor;
        }

        public Object process(Object msg, Message message, Channel channel) {
            return processor.process(msg, message, channel);
        }
    }
}
