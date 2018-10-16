package top.arkstack.shine.mq;

import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import top.arkstack.shine.mq.bean.SendTypeEnum;
import top.arkstack.shine.mq.template.Template;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * topic 模式测试
 *
 * @author 7le
 * @version 1.0.0
 */
public class RabbitTopicTest {

    private Template template;

    public RabbitTopicTest() {
    }

    @Before
    public void init() throws Exception {
        RabbitProperties properties = new RabbitProperties();
        properties.setHost("127.0.0.1");
        properties.setUsername("guest");
        properties.setPassword("guest");
        RabbitmqProperties mqProperties = new RabbitmqProperties();
        mqProperties.setListenerEnable(true);
        RabbitmqFactory factory = RabbitmqFactory.getInstance(mqProperties,
                new CachingConnectionFactory(Objects.requireNonNull(this.getRabbitConnectionFactoryBean(properties).getObject())));
        this.template = factory.getTemplate();
        factory.add("shine-queue-topic", "shine-exchange-topic", "shine", new RabbitTest.ProcessorTest(), SendTypeEnum.TOPIC);
    }

    @Test
    public void send() throws Exception {
        for(int i = 0; i < 5; ++i) {
            this.template.send("shine-exchange-topic", "shine " + i, "shine", 0, 0, SendTypeEnum.TOPIC);
        }

        TimeUnit.SECONDS.sleep(600L);
    }

    private RabbitConnectionFactoryBean getRabbitConnectionFactoryBean(RabbitProperties properties) throws Exception {
        PropertyMapper map = PropertyMapper.get();
        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
        properties.getClass();
        map.from(properties::determineHost).whenNonNull().to(factory::setHost);
        properties.getClass();
        map.from(properties::determinePort).to(factory::setPort);
        properties.getClass();
        map.from(properties::determineUsername).whenNonNull().to(factory::setUsername);
        properties.getClass();
        map.from(properties::determinePassword).whenNonNull().to(factory::setPassword);
        properties.getClass();
        map.from(properties::determineVirtualHost).whenNonNull().to(factory::setVirtualHost);
        properties.getClass();
        map.from(properties::getRequestedHeartbeat).whenNonNull().asInt(Duration::getSeconds).to(factory::setRequestedHeartbeat);
        RabbitProperties.Ssl ssl = properties.getSsl();
        if(ssl.isEnabled()) {
            factory.setUseSSL(true);
            ssl.getClass();
            map.from(ssl::getAlgorithm).whenNonNull().to(factory::setSslAlgorithm);
            ssl.getClass();
            map.from(ssl::getKeyStoreType).to(factory::setKeyStoreType);
            ssl.getClass();
            map.from(ssl::getKeyStore).to(factory::setKeyStore);
            ssl.getClass();
            map.from(ssl::getKeyStorePassword).to(factory::setKeyStorePassphrase);
            ssl.getClass();
            map.from(ssl::getTrustStoreType).to(factory::setTrustStoreType);
            ssl.getClass();
            map.from(ssl::getTrustStore).to(factory::setTrustStore);
            ssl.getClass();
            map.from(ssl::getTrustStorePassword).to(factory::setTrustStorePassphrase);
        }

        properties.getClass();
        map.from(properties::getConnectionTimeout).whenNonNull().asInt(Duration::toMillis).to(factory::setConnectionTimeout);
        factory.afterPropertiesSet();
        return factory;
    }
}
