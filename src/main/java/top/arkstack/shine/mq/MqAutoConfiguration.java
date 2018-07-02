package top.arkstack.shine.mq;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author 7le
 * @version 1.0.0
 */
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
}
