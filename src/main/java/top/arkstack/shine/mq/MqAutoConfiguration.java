package top.arkstack.shine.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.arkstack.shine.mq.annotation.DistributedTransAspect;
import top.arkstack.shine.mq.coordinator.Coordinator;
import top.arkstack.shine.mq.coordinator.RedisCoordinator;

/**
 * @author 7le
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties
@Import({RabbitAutoConfiguration.class, DistributedTransAspect.class})
public class MqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RabbitmqProperties mqProperties() {
        return new RabbitmqProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitmqFactory rabbitmqFactory(RabbitmqProperties mqProperties, CachingConnectionFactory factory) {
        return RabbitmqFactory.getInstance(mqProperties, factory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisCoordinator redisCoordinator() {
        return new RedisCoordinator();
    }

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    @ConditionalOnProperty(name = "shine.mq.distributed.transaction", havingValue = "true")
    public RabbitTemplate rabbitmqTemplate(RabbitmqFactory rabbitmqFactory) {
        RabbitTemplate template = rabbitmqFactory.getRabbitTemplate();
        //消息发送到RabbitMQ交换器后接收ack回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData != null) {
                log.info("ConfirmCallback ack: {} correlationData: {} cause: {}", ack, correlationData, cause);
                String msgId = correlationData.getId();
                //消息能投入正确的消息队列，并持久化，返回的ack为true
                if (ack) {
                    log.info("The message has been successfully delivered to the queue, correlationData:{}", correlationData);
                    String name = ((CorrelationDataExt) correlationData).getCoordinator();
                    Coordinator coordinator = (Coordinator) applicationContext.getBean(name);
                    coordinator.delStatus(msgId);
                } else {
                    log.error("Message delivery failed, bizId: {}, cause: {}", correlationData.getId(), cause);
                    //TODO retry
                }
            }
        });
        //使用return-callback时必须设置mandatory为true
        template.setMandatory(true);
        //消息发送到RabbitMQ交换器，但无相应Exchange时的回调
        template.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String messageId = message.getMessageProperties().getMessageId();
            log.error("ReturnCallback exception, no matching queue found. message id: {}, replyCode: {}, replyText: {},"
                    + "exchange: {}, routingKey: {}", messageId, replyCode, replyText, exchange, routingKey);
        });
        return template;
    }
}
