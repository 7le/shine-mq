# shine-mq

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/top.arkstack/shine-mq/badge.svg)](https://maven-badges.herokuapp.com/maven-central/top.arkstack/shine-mq)

### 🐳 maven

```
<dependency>
  <groupId>top.arkstack</groupId>
  <artifactId>shine-web</artifactId>
  <version>1.0.7-SNAPSHOT</version>
</dependency>
```

### 🌈 使用文档

支持springboot的配置，具体可配置的参数如下：

```
    /**
     * 允许空闲的最大通道数
     */
    private int channelCacheSize = 25;

    /**
     * 建立连接的超时时间
     */
    private int connectionTimeout = 0;

    /**
     * 每次从队列中取几条，只有等收到Ack了才重新取
     * 0为循环调度 1为公平调度
     */
    private int prefetchSize = 1;

    /**
     * {@link org.springframework.amqp.core.AcknowledgeMode}
     *
     * 0 AUTO
     * 1 MANUAL
     * 2 NONE
     */
    private int acknowledgeMode = 0;

    /**
     * 消费者数量 缺省为CPU核数*2
     */
    private int processSize = Runtime.getRuntime().availableProcessors() << 1;

    /**
     * 是否持久化，指是否保存到erlang自带得数据库mnesia中，即重启服务是否消失
     */
    private boolean durable = true;

    /**
     * 是否排外，指当前定义的队列是connection中的channel共享的，其他connection连接访问不到
     */
    private boolean exclusive = false;

    /**
     * 是否自动删除，指当connection.close时队列删除
     */
    private boolean autoDelete = false;

    /**
     * 是否初始化消息监听者， 若服务只是Producer则关闭
     */
    private boolean listenerEnable = false;
```

**rabbitmq**的配置复用spring的配置

```
spring:
  rabbitmq:
    host: 114.215.122.xxx
    username: xxxxx
    password: xxxxx
```

如果需要开启消费者的服务的话，设置**listener-enable**参数为**true**，默认为**false**，以yml举例如下：

```
shine:
  mq:
    rabbit:
      listener-enable: true
```

对于生产者，demo如下，``RabbitmqFactory``已经注入spring容器，可以直接通过``@Autowired``获得。

通过**rabbitmqFactory.add**可以实现动态增加生产者和消费者。

```
@Component
public class Producer {

    @Autowired
    RabbitmqFactory rabbitmqFactory;

    @PostConstruct
    public void pull() throws Exception {
        rabbitmqFactory.add("queue-test", "exchange_test", "yoyo", null);
        rabbitmqFactory.start();
        for (int i = 0; i < 50; i++) {
            rabbitmqFactory.getTemplate().send("queue-test", "exchange-test", "hello world "+i, "yoyo");
        }
        System.out.println("------------------------pull end-------------------------------");
    }
}
```

对于消费者，demo如下，``Processor``需要自己实现，这里写获得消息后的业务处理。

```
@Component
public class Consumer {

    @Autowired
    RabbitmqFactory rabbitmqFactory;

    @PostConstruct
    public void pull() throws Exception {
        rabbitmqFactory.add("queue_test", "exchange_test", "yoyo", new ProcessorTest());
        rabbitmqFactory.start();
    }

    static class ProcessorTest extends BaseProcessor {
    
        @Override
        public Object process(Object msg, Message message, Channel channel) {
            System.out.println(" process: " + msg);
            try {
                TimeUnit.SECONDS.sleep(10);
                //如果选择了MANUAL模式 需要手动回执ack
                //channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }
}
```
