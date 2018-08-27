package top.arkstack.shine.mq;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import top.arkstack.shine.mq.bean.EventMessage;
import top.arkstack.shine.mq.bean.SendTypeEnum;
import top.arkstack.shine.mq.processor.Processor;

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
public class MessageAdapterHandler implements ChannelAwareMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageAdapterHandler.class);

    private ConcurrentMap<String, ProcessorWrap> map;

    private ConcurrentMap<String, ProcessorWrap> topicMap;

    protected MessageAdapterHandler() {
        this.map = new ConcurrentHashMap<>();
        this.topicMap = new ConcurrentHashMap<>();
    }

    protected void add(String queueName, String exchangeName, String routingKey,
                       Processor processor, SendTypeEnum type, MessageConverter messageConverter) {

        Objects.requireNonNull(queueName, "The queueName is empty.");
        Objects.requireNonNull(exchangeName, "The exchangeName is empty.");
        Objects.requireNonNull(messageConverter, "The messageConverter is empty.");
        Objects.requireNonNull(routingKey, "The routingKey is empty.");

        ProcessorWrap pw = new ProcessorWrap(messageConverter, processor);
        if (type != null && SendTypeEnum.TOPIC == type) {
            topicMap.putIfAbsent(exchangeName + "_" + routingKey + "_" + type, pw);
        }
        ProcessorWrap oldProcessorWrap = map.putIfAbsent(queueName + "_" + exchangeName + "_" + routingKey + "_" +
                (type == null ? SendTypeEnum.DIRECT.toString() : type.toString()), pw);
        if (oldProcessorWrap != null) {
            logger.warn("The processor of this queue and exchange exists");
        }
    }

    @Override
    public void onMessage(Message message, Channel channel) {
        EventMessage em;
        try {
            em = JSON.parseObject(message.getBody(), EventMessage.class);
            ProcessorWrap wrap;
            if (SendTypeEnum.TOPIC.toString().equals(em.getSendTypeEnum())) {
                wrap = topicMap.get(em.getExchangeName() + "_" + em.getRoutingKey() + "_" + em.getSendTypeEnum());
            } else {
                wrap = map.get(em.getQueueName() + "_" + em.getExchangeName() + "_" + em.getRoutingKey()
                        + "_" + em.getSendTypeEnum());
            }
            wrap.process(em.getData(), message, channel);
        } catch (Exception e) {
            //TODO 后续可以提供回调，供使用者自定义
            log.error("MessageAdapterHandler {} error :", message.getBody(), e);
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
