package top.arkstack.shine.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author 7le
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties
@Import(RabbitAutoConfiguration.class)
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
    @ConditionalOnProperty(name = "shine.mq.Distributed.transaction", havingValue = "true")
    public RabbitTemplate rabbitmqTemplate(RabbitmqFactory rabbitmqFactory) {
        RabbitTemplate template = rabbitmqFactory.getRabbitTemplate();
        //消息发送到RabbitMQ交换器后接收ack回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            //消息能投入正确的消息队列，并持久化，返回的ack为true
            if (ack) {
                log.info("消息已成功投递到队列, correlationData:{}", correlationData);
                //TODO 清除持久化
            } else {
                log.error("消息投递失败,业务号:{}，原因:{}", correlationData.getId(), cause);
            }
        });
        //消息发送到RabbitMQ交换器，但无相应Exchange时的回调
        template.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {

        });
        return template;
    }
}
