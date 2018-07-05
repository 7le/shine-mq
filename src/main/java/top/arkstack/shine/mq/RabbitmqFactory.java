package top.arkstack.shine.mq;

import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.util.StringUtils;
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

    private static RabbitTemplate rabbitTemplate;

    private Template template;

    private MessageAdapterHandler msgAdapterHandler = new MessageAdapterHandler();

    private SimpleMessageListenerContainer listenerContainer;

    private Map<String, Queue> queues = new HashMap<>();

    private Set<String> bind = new HashSet<>();

    private Map<String, DirectExchange> exchanges = new HashMap<>();

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
            declareBinding(relaArr[0], relaArr[1], relaArr[2]);
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
        MessageListener listener = new MessageListenerAdapter(msgAdapterHandler, "receive");
        listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(rabbitConnectionFactory);
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        listenerContainer.setMessageListener(listener);
        listenerContainer.setErrorHandler(new MessageErrorHandler());
        listenerContainer.setPrefetchCount(config.getPrefetchSize());
        listenerContainer.setConcurrentConsumers(config.getProcessSize());
        listenerContainer.setTxSize(config.getPrefetchSize());
        listenerContainer.setQueues(queues.values().toArray(new Queue[queues.size()]));
        listenerContainer.start();
    }

    @Override
    public Factory add(String queueName, String exchangeName, String routingKey, Processor processor) {
        return add(queueName, exchangeName, routingKey, processor, serializerMessageConverter);
    }

    @Override
    public Factory add(String queueName, String exchangeName, String routingKey, Processor processor, MessageConverter messageConverter) {
        msgAdapterHandler.add(queueName, exchangeName, routingKey, processor, messageConverter);
        if (isStarted.get() && config.isListenerEnable()) {
            declareBinding(queueName, exchangeName, routingKey);
            listenerContainer.setQueues(queues.values().toArray(new Queue[queues.size()]));
        }
        return this;
    }

    private synchronized void declareBinding(String queueName, String exchangeName, String routingKey) {
        String bindRelation = queueName + "_" + exchangeName + "_" + routingKey;
        if (bind.contains(bindRelation)) {
            return;
        }
        boolean needBinding = false;
        DirectExchange directExchange = exchanges.get(exchangeName);
        if (directExchange == null) {
            directExchange = new DirectExchange(exchangeName, config.isDurable(), config.isAutoDelete(), null);
            exchanges.put(exchangeName, directExchange);
            rabbitAdmin.declareExchange(directExchange);
            needBinding = true;
        }
        Queue queue = queues.get(queueName);
        if (queue == null) {
            queue = new Queue(queueName, config.isDurable(), config.isExclusive(), config.isAutoDelete());
            queues.put(queueName, queue);
            rabbitAdmin.declareQueue(queue);
            needBinding = true;
        }
        if (needBinding) {
            Binding binding = BindingBuilder.bind(queue).to(directExchange).with(routingKey);
            rabbitAdmin.declareBinding(binding);
            bind.add(bindRelation);
        }
    }

}
