package top.arkstack.shine.mq;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import top.arkstack.shine.mq.bean.EventMessage;
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
public class MessageAdapterHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageAdapterHandler.class);

    private ConcurrentMap<String, ProcessorWrap> map;

    protected MessageAdapterHandler() {
        this.map = new ConcurrentHashMap<>();
    }

    protected void add(String queueName, String exchangeName, String routingKey,
                       Processor processor, MessageConverter messageConverter) {

        Objects.requireNonNull(queueName, "The queueName is empty.");
        Objects.requireNonNull(exchangeName, "The exchangeName is empty.");
        Objects.requireNonNull(messageConverter, "The messageConverter is empty.");
        Objects.requireNonNull(routingKey, "The routingKey is empty.");

        ProcessorWrap pw = new ProcessorWrap(messageConverter, processor);
        ProcessorWrap oldProcessorWrap = map.putIfAbsent(queueName + "_" + exchangeName + "_" + routingKey, pw);
        if (oldProcessorWrap != null) {
            logger.warn("The processor of this queue and exchange exists");
        }
    }

    public void receive(byte[] msg) {
        EventMessage message = JSON.parseObject(new String(msg), EventMessage.class);
        ProcessorWrap wrap = map.get(message.getQueueName() + "_" + message.getExchangeName() + "_" + message.getRoutingKey());
        wrap.process(message.getData());
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

        public Object process(Object msg) {
            return processor.process(msg);
        }
    }
}
