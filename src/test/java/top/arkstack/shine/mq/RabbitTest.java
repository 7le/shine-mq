package top.arkstack.shine.mq;

import com.rabbitmq.client.Channel;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import top.arkstack.shine.mq.processor.BaseProcessor;
import top.arkstack.shine.mq.template.Template;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RabbitTest {

    private Template template;


    @Before
    public void init() throws Exception {
        RabbitProperties properties = new RabbitProperties();
        RabbitmqProperties mqProperties = new RabbitmqProperties();
        RabbitmqFactory factory = RabbitmqFactory.getInstance(mqProperties,
                new CachingConnectionFactory(getRabbitConnectionFactoryBean(properties).getObject()));
        template = factory.getTemplate();
        factory.add("shine-queue", "shine-exchange", "shine", new ProcessorTest(), null);
        factory.start();
    }

    @Test
    public void send() throws Exception {

        for (int i = 0; i < 50; i++) {
            template.send("shine-queue", "shine-exchange", "shine " + i, "shine");
        }

        TimeUnit.SECONDS.sleep(600);
    }

    private RabbitConnectionFactoryBean getRabbitConnectionFactoryBean(
            RabbitProperties properties) throws Exception {
        PropertyMapper map = PropertyMapper.get();
        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
        map.from(properties::determineHost).whenNonNull().to(factory::setHost);
        map.from(properties::determinePort).to(factory::setPort);
        map.from(properties::determineUsername).whenNonNull()
                .to(factory::setUsername);
        map.from(properties::determinePassword).whenNonNull()
                .to(factory::setPassword);
        map.from(properties::determineVirtualHost).whenNonNull()
                .to(factory::setVirtualHost);
        map.from(properties::getRequestedHeartbeat).whenNonNull()
                .asInt(Duration::getSeconds).to(factory::setRequestedHeartbeat);
        RabbitProperties.Ssl ssl = properties.getSsl();
        if (ssl.isEnabled()) {
            factory.setUseSSL(true);
            map.from(ssl::getAlgorithm).whenNonNull().to(factory::setSslAlgorithm);
            map.from(ssl::getKeyStoreType).to(factory::setKeyStoreType);
            map.from(ssl::getKeyStore).to(factory::setKeyStore);
            map.from(ssl::getKeyStorePassword).to(factory::setKeyStorePassphrase);
            map.from(ssl::getTrustStoreType).to(factory::setTrustStoreType);
            map.from(ssl::getTrustStore).to(factory::setTrustStore);
            map.from(ssl::getTrustStorePassword).to(factory::setTrustStorePassphrase);
        }
        map.from(properties::getConnectionTimeout).whenNonNull()
                .asInt(Duration::toMillis).to(factory::setConnectionTimeout);
        factory.afterPropertiesSet();
        return factory;
    }

    static class ProcessorTest extends BaseProcessor {

        @Override
        public Object process(Object msg, Message message, Channel channel) {
            System.out.println(" process: " + msg);
            try {
                TimeUnit.SECONDS.sleep(10);
                //如果选择了MANUAL模式 需要手动回执ack
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }
}
