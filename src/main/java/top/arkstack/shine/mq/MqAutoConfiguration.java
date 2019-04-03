package top.arkstack.shine.mq;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import top.arkstack.shine.mq.annotation.DistributedTransAspect;
import top.arkstack.shine.mq.coordinator.redis.RedisCoordinator;
import top.arkstack.shine.mq.coordinator.redis.RedisUtil;

/**
 * @author 7le
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties
@Import({RabbitAutoConfiguration.class, DistributedTransAspect.class, MessageAdapterHandler.class})
public class MqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MqProperties mqProperties() {
        return new MqProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitmqFactory rabbitmqFactory(MqProperties properties, CachingConnectionFactory factory) {
        return RabbitmqFactory.getInstance(properties, factory);
    }

    @Configuration
    @ConditionalOnProperty(name = "shine.mq.distributed.transaction", havingValue = "true")
    public class RedisConfiguration {

        @Bean
        @ConditionalOnProperty(name = "shine.mq.distributed.redis-persistence",
                havingValue = "true", matchIfMissing = true)
        public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);

            // 使用Jackson2JsonRedisSerialize 替换默认序列化
            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

            jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

            // 设置value的序列化规则和 key的序列化规则
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        }

        @Bean
        @ConditionalOnProperty(name = "shine.mq.distributed.redis-persistence",
                havingValue = "true", matchIfMissing = true)
        public RedisCoordinator redisCoordinator() {
            return new RedisCoordinator();
        }

        @Bean
        @ConditionalOnProperty(name = "shine.mq.distributed.redis-persistence",
                havingValue = "true", matchIfMissing = true)
        public RedisUtil redisUtil() {
            return new RedisUtil();
        }
    }
}
