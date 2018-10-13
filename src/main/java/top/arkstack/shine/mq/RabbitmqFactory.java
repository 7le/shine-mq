package top.arkstack.shine.mq;

import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import top.arkstack.shine.mq.bean.SendTypeEnum;
import top.arkstack.shine.mq.processor.Processor;
import top.arkstack.shine.mq.template.RabbitmqTemplate;
import top.arkstack.shine.mq.template.Template;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * rabbitmq工厂
 * 提供rabbitmq的初始化，以及exchange和queue的添加
 *
 * @author 7le
 * @version 1.0.0
 */
@Data
public class RabbitmqFactory implements Factory {

    private static RabbitmqFactory rabbitmqFactory;

    private RabbitmqProperties config;

    private static CachingConnectionFactory rabbitConnectionFactory;

    private RabbitAdmin rabbitAdmin;

    protected RabbitTemplate rabbitTemplate;

    private Template template;

    private MessageAdapterHandler msgAdapterHandler = new MessageAdapterHandler();

    private SimpleMessageListenerContainer listenerContainer;

    private Map<String, Queue> queues = new HashMap<>();

    private Set<String> bind = new HashSet<>();

    private Map<String, Exchange> exchanges = new HashMap<>();

    private AtomicBoolean isStarted = new AtomicBoolean(false);

    /**
     * 缺省序列化方式 Jackson2JsonMessageConverter
     */
    private MessageConverter serializerMessageConverter = new Jackson2JsonMessageConverter();


    private RabbitmqFactory(RabbitmqProperties config) {
        Objects.requireNonNull(config, "The RabbitmqProperties is empty.");
        this.config = config;
        rabbitAdmin = new RabbitAdmin(rabbitConnectionFactory);
        rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);
        rabbitTemplate.setMessageConverter(serializerMessageConverter);
        template = new RabbitmqTemplate(rabbitTemplate, serializerMessageConverter);
    }

    @Override
    public void start() {
        if (isStarted.get()) {
            return;
        }
        Set<String> mapping = msgAdapterHandler.getAllBinding();
        for (String relation : mapping) {
            String[] relaArr = relation.split("_");
            declareBinding(relaArr[0], relaArr[1], relaArr[2], true, relaArr[3]);
        }
        if (config.isListenerEnable()) {
            initMsgListenerAdapter();
        }
        isStarted.set(true);
    }

    public synchronized static RabbitmqFactory getInstance(RabbitmqProperties config, CachingConnectionFactory factory) {
        rabbitConnectionFactory = factory;
        if (rabbitmqFactory == null) {
            rabbitmqFactory = new RabbitmqFactory(config);
        }
        return rabbitmqFactory;
    }

    /**
     * 初始化消息监听器容器
     */
    private void initMsgListenerAdapter() {
        listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(rabbitConnectionFactory);
        if (config.getAcknowledgeMode() == 1) {
            listenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        } else {
            listenerContainer.setAcknowledgeMode(
                    config.getAcknowledgeMode() == 2 ? AcknowledgeMode.NONE : AcknowledgeMode.AUTO);
        }
        listenerContainer.setMessageListener(msgAdapterHandler);
        listenerContainer.setErrorHandler(new MessageErrorHandler());
        listenerContainer.setPrefetchCount(config.getPrefetchSize());
        listenerContainer.setConcurrentConsumers(config.getProcessSize());
        listenerContainer.setTxSize(config.getPrefetchSize());
        listenerContainer.setQueues(queues.values().toArray(new Queue[queues.size()]));
        listenerContainer.start();
    }

    @Override
    public Factory add(String queueName, String exchangeName, String routingKey, Processor processor, SendTypeEnum type) {
        return add(queueName, exchangeName, routingKey, processor, type, serializerMessageConverter);
    }

    @Override
    public Factory add(String queueName, String exchangeName, String routingKey, Processor processor, SendTypeEnum type,
                       MessageConverter messageConverter) {
        if (processor != null) {
            msgAdapterHandler.add(queueName, exchangeName, routingKey, processor, type, messageConverter);
            if (isStarted.get() && config.isListenerEnable()) {
                declareBinding(queueName, exchangeName, routingKey, true,
                        (type == null ? SendTypeEnum.DIRECT.toString() : type.toString()));
                listenerContainer.setQueues(queues.values().toArray(new Queue[queues.size()]));
            }
            return this;
        } else {
            declareBinding(queueName, exchangeName, routingKey, false, type.toString());
            return this;
        }
    }

    private synchronized void declareBinding(String queueName, String exchangeName, String routingKey,
                                             boolean isPutQueue, String type) {
        String bindRelation = queueName + "_" + exchangeName + "_" + routingKey + "_" + type;
        if (bind.contains(bindRelation)) {
            return;
        }
        boolean needBinding = false;
        Exchange exchange = exchanges.get(exchangeName);
        if (exchange == null) {
            if (SendTypeEnum.TOPIC.toString().equals(type)) {
                exchange = new TopicExchange(exchangeName, config.isDurable(), config.isAutoDelete(), null);
            } else {
                exchange = new DirectExchange(exchangeName, config.isDurable(), config.isAutoDelete(), null);
            }
            exchanges.put(exchangeName, exchange);
            rabbitAdmin.declareExchange(exchange);
            needBinding = true;
        }
        Queue queue = queues.get(queueName);
        if (queue == null) {
            queue = new Queue(queueName, config.isDurable(), config.isExclusive(), config.isAutoDelete());
            if (isPutQueue) {
                queues.put(queueName, queue);
            }
            rabbitAdmin.declareQueue(queue);
            needBinding = true;
        }
        if (needBinding) {
            Binding binding;
            if (SendTypeEnum.TOPIC.toString().equals(type)) {
                binding = BindingBuilder.bind(queue).to((TopicExchange) exchange).with(routingKey);
            } else {
                binding = BindingBuilder.bind(queue).to((DirectExchange) exchange).with(routingKey);
            }
            rabbitAdmin.declareBinding(binding);
            bind.add(bindRelation);
        }
    }

}
