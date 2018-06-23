package top.arkstack.shine.mq;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 7le
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties
public class MqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RabbitmqProperties mqProperties() {
        return new RabbitmqProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitmqFactory rabbitmqFactory(RabbitmqProperties mqProperties) {
        return RabbitmqFactory.getInstance(mqProperties);
    }
}
