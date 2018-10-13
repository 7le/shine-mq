package top.arkstack.shine.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息队列的基础配置信息
 *
 * @author 7le
 * @version 1.1.0
 */
@Data
@ConfigurationProperties("shine.mq")
public class MqProperties {

    public static class Distributed{

        /**
         * 是否初始化 开启分布式事务 缺省为false
         */
        private boolean transaction = false;
    }

}
