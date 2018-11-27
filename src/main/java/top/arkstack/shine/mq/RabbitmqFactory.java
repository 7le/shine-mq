package top.arkstack.shine.mq;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import top.arkstack.shine.mq.bean.EventMessage;
import top.arkstack.shine.mq.bean.SendTypeEnum;
import top.arkstack.shine.mq.constant.MqConstant;
import top.arkstack.shine.mq.coordinator.Coordinator;
import top.arkstack.shine.mq.processor.Processor;
import top.arkstack.shine.mq.template.RabbitmqTemplate;
import top.arkstack.shine.mq.template.Template;

import java.util.*;

/**
 * rabbitmq工厂
 * 提供rabbitmq的初始化，以及exchange和queue的添加
 *
 * @author 7le
 * @version 1.0.0
 */
@Data
@Slf4j
public class RabbitmqFactory implements Factory {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    MessageAdapterHandler msgAdapterHandler;

    private static RabbitmqFactory rabbitmqFactory;

    private MqProperties config;

    private MqProperties.Rabbit rabbit;

    private static CachingConnectionFactory rabbitConnectionFactory;

    private RabbitAdmin rabbitAdmin;

    protected RabbitTemplate rabbitTemplate;

    private Template template;

    private DirectMessageListenerContainer listenerContainer;

    private Map<String, Queue> queues = new HashMap<>();

    private Set<String> bind = new HashSet<>();

    private Map<String, Exchange> exchanges = new HashMap<>();

    /**
     * 缺省序列化方式 Jackson2JsonMessageConverter
     */
    private MessageConverter serializerMessageConverter = new Jackson2JsonMessageConverter();


    private RabbitmqFactory(MqProperties config) {
        Objects.requireNonNull(config, "The RabbitmqProperties is empty.");
        this.config = config;
        this.rabbit = config.getRabbit();
        rabbitAdmin = new RabbitAdmin(rabbitConnectionFactory);
        rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);
        rabbitTemplate.setMessageConverter(serializerMessageConverter);
        if (config.getDistributed().isTransaction()) {
            setRabbitTemplateForDis(config);
        }
        template = new RabbitmqTemplate(rabbitTemplate, serializerMessageConverter);
    }

    public synchronized static RabbitmqFactory getInstance(MqProperties config, CachingConnectionFactory factory) {
        rabbitConnectionFactory = factory;
        //设置生成者确认机制
        rabbitConnectionFactory.setPublisherConfirms(true);
        if (config.getRabbit().getChannelCacheSize() != null) {
            rabbitConnectionFactory.setConnectionCacheSize(config.getRabbit().getChannelCacheSize());
        }
        if (rabbitmqFactory == null) {
            rabbitmqFactory = new RabbitmqFactory(config);
        }
        return rabbitmqFactory;
    }

    /**
     * 初始化消息监听器容器
     */
    private void initMsgListenerAdapter() {
        listenerContainer = new DirectMessageListenerContainer();
        listenerContainer.setConnectionFactory(rabbitConnectionFactory);
        if (rabbit.getAcknowledgeMode() == 1) {
            listenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        } else {
            listenerContainer.setAcknowledgeMode(
                    rabbit.getAcknowledgeMode() == 2 ? AcknowledgeMode.NONE : AcknowledgeMode.AUTO);
        }
        listenerContainer.setMessageListener(msgAdapterHandler);
        listenerContainer.setErrorHandler(new MessageErrorHandler());
        if (rabbit.getPrefetchCount() != null) {
            listenerContainer.setPrefetchCount(rabbit.getPrefetchCount());
        }
        if (rabbit.getConsumersPerQueue() != null) {
            listenerContainer.setConsumersPerQueue(rabbit.getConsumersPerQueue());
        }
        listenerContainer.setQueues(queues.values().toArray(new Queue[queues.size()]));
        listenerContainer.start();
    }

    private void setRabbitTemplateForDis(MqProperties config) {
        if (config.getRabbit().getAcknowledgeMode() != 1) {
            throw new ShineMqException("Distributed transactions must use MANUAL(AcknowledgeMode=1) mode!");
        }
        //消息发送到RabbitMQ交换器后接收ack回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData != null) {
                log.info("ConfirmCallback ack: {} correlationData: {} cause: {}", ack, correlationData, cause);
                String msgId = correlationData.getId();
                CorrelationDataExt ext = (CorrelationDataExt) correlationData;
                Coordinator coordinator = (Coordinator) applicationContext.getBean(ext.getCoordinator());
                //消息能投入正确的消息队列，并持久化，返回的ack为true
                if (ack) {
                    log.info("The message has been successfully delivered to the queue, correlationData:{}", correlationData);
                    coordinator.delStatus(msgId);
                } else {
                    //失败了判断重试次数，重试次数大于0则继续发送
                    if (ext.getMaxRetries() > 0) {
                        try {
                            rabbitmqFactory.setCorrelationData(msgId, ext.getCoordinator(), ext.getMessage(),
                                    ext.getMaxRetries() - 1);
                            rabbitmqFactory.getTemplate().send(ext.getMessage().getExchangeName(), ext.getMessage(),
                                    ext.getMessage().getRoutingKey());
                        } catch (Exception e) {
                            log.error("Message retry failed to send, message:{} exception: ", ext.getMessage(), e);
                        }
                    } else {
                        log.error("Message delivery failed, msgId: {}, cause: {}", msgId, cause);
                    }
                }
            }
        });
        //使用return-callback时必须设置mandatory为true
        rabbitTemplate.setMandatory(true);
        //消息发送到RabbitMQ交换器，但无相应Exchange时的回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String messageId = message.getMessageProperties().getMessageId();
            log.error("ReturnCallback exception, no matching queue found. message id: {}, replyCode: {}, replyText: {},"
                    + "exchange: {}, routingKey: {}", messageId, replyCode, replyText, exchange, routingKey);
        });
    }

    @Override
    public Factory add(String queueName, String exchangeName, String routingKey, Processor processor, SendTypeEnum type) {
        return add(queueName, exchangeName, routingKey, processor, type, serializerMessageConverter);
    }

    @Override
    public Factory add(String queueName, String exchangeName, String routingKey, Processor processor, SendTypeEnum type,
                       MessageConverter messageConverter) {
        if (processor != null) {
            msgAdapterHandler.add(exchangeName, routingKey, processor, type, messageConverter);
            if (rabbit.isListenerEnable()) {
                declareBinding(queueName, exchangeName, routingKey, true,
                        type == null ? SendTypeEnum.DIRECT.toString() : type.toString(), false);
                if (listenerContainer == null) {
                    initMsgListenerAdapter();
                } else {
                    listenerContainer.addQueueNames(queueName);
                }
            } else {
                declareBinding(queueName, exchangeName, routingKey, false,
                        type == null ? SendTypeEnum.DIRECT.toString() : type.toString(), false);
            }
            return this;
        } else {
            declareBinding(queueName, exchangeName, routingKey, false,
                    type == null ? SendTypeEnum.DIRECT.toString() : type.toString(), false);
            return this;
        }
    }

    public Factory addDLX(String queueName, String exchangeName, String routingKey, Processor processor, SendTypeEnum type) {
        return addDLX(queueName, exchangeName, routingKey, processor, type, serializerMessageConverter);
    }

    public Factory addDLX(String queueName, String exchangeName, String routingKey, Processor processor, SendTypeEnum type,
                          MessageConverter messageConverter) {
        if (processor != null) {
            msgAdapterHandler.add(exchangeName, routingKey, processor, type, messageConverter);
            if (rabbit.isListenerEnable()) {
                declareBinding(queueName, exchangeName, routingKey, true,
                        type == null ? SendTypeEnum.DIRECT.toString() : type.toString(), true);
                if (listenerContainer == null) {
                    initMsgListenerAdapter();
                } else {
                    listenerContainer.addQueueNames(queueName);
                }
            } else {
                declareBinding(queueName, exchangeName, routingKey, false,
                        type == null ? SendTypeEnum.DIRECT.toString() : type.toString(), true);
            }
            return this;
        } else {
            declareBinding(queueName, exchangeName, routingKey, false,
                    type == null ? SendTypeEnum.DIRECT.toString() : type.toString(), true);
            return this;
        }
    }

    private synchronized void declareBinding(String queueName, String exchangeName, String routingKey,
                                             boolean isPutQueue, String type, boolean isDlx) {
        String bindRelation = queueName + "_" + exchangeName + "_" + routingKey + "_" + type;
        if (bind.contains(bindRelation)) {
            return;
        }
        boolean needBinding = false;
        Exchange exchange = exchanges.get(exchangeName);
        if (exchange == null) {
            if (SendTypeEnum.TOPIC.toString().equals(type)) {
                exchange = new TopicExchange(exchangeName, rabbit.isDurable(), rabbit.isAutoDelete(), null);
            } else {
                exchange = new DirectExchange(exchangeName, rabbit.isDurable(), rabbit.isAutoDelete(), null);
            }
            exchanges.put(exchangeName, exchange);
            rabbitAdmin.declareExchange(exchange);
            needBinding = true;
        }
        Queue queue = queues.get(queueName);
        if (queue == null) {
            if (isDlx) {
                Map<String, Object> args = new HashMap<>(2);
                args.put("x-dead-letter-exchange", MqConstant.DEAD_LETTER_EXCHANGE);
                args.put("x-dead-letter-routing-key", MqConstant.DEAD_LETTER_ROUTEKEY);
                queue = new Queue(queueName, rabbit.isDurable(), rabbit.isExclusive(), rabbit.isAutoDelete(), args);
            } else {
                queue = new Queue(queueName, rabbit.isDurable(), rabbit.isExclusive(), rabbit.isAutoDelete());
            }
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

    /**
     * 扩展消息的CorrelationData，方便在回调中应用
     */
    public void setCorrelationData(String bizId, String coordinator, EventMessage msg, Integer retry) {
        rabbitTemplate.setCorrelationDataPostProcessor(((message, correlationData) ->
                new CorrelationDataExt(bizId, coordinator,
                        retry == null ? config.getDistributed().getCommitMaxRetries() : retry, msg)));
    }
}
