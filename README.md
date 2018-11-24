# shine-mq

[![Gitter](https://badges.gitter.im/7le/shine-mq.svg)](https://gitter.im/7le/shine-mq)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/top.arkstack/shine-mq/badge.svg)](https://search.maven.org/artifact/top.arkstack/shine-mq/)
[![Latest release](https://img.shields.io/github/release/7le/shine-mq.svg)](https://github.com/7le/shine-mq/releases/latest)

### 🐳 maven

```
<dependency>
    <groupId>top.arkstack</groupId>
    <artifactId>shine-mq</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

### 🐣 主要功能

* 封装mq的操作，方便使用
* 实现基于可靠消息服务的分布式事务 

### 🌈 使用文档

对应的演示demo可以戳 [shine-mq-demo](https://github.com/7le/shine-mq-demo)

分布式事务支持springboot配置，具体可配置的参数如下：

```
    /**
     * 是否初始化 开启分布式事务 缺省为false
     */
    private boolean transaction = false;

    /**
     * 提交ack 失败最大重试次数
     */
    private Integer commitMaxRetries = 3;

    /**
     * 接收消息 ack 失败最大尝试次数
     */
    private Integer receiveMaxRetries = 3;

    /**
     * 默认提供redis中间件来实现消息提交到mq之前的持久化
     *
     * 也可以自己实现 {@link top.arkstack.shine.mq.coordinator.Coordinator}
     * 或者不想用redis，可以设置为false，就不会有redis的依赖
     */
    private boolean redisPersistence = true;

```

封装mq的操作目前兼容Direct和Topic模式，可配置的参数如下：

```
    /**
     * 是否初始化消息监听者， 若服务只是Producer则关闭
     */
    private boolean listenerEnable = false;
    /**
     * {@link org.springframework.amqp.core.AcknowledgeMode}
     * <p>
     * 0 AUTO
     * 1 MANUAL
     * 2 NONE
     */
    private int acknowledgeMode = 1;

    /**
     * 每个消费者可能未完成的未确认消息的数量。
     */
    private Integer prefetchCount = null;

    /**
     * 为每个已配置队列创建的消费者数
     */
    private Integer consumersPerQueue = null;

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

    /**
     * 通道缓存
     */
    private Integer channelCacheSize = null;
```
