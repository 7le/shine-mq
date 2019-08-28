# Shine-mq

[![Gitter](https://badges.gitter.im/7le/shine-mq.svg)](https://gitter.im/7le/shine-mq)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/top.arkstack/shine-mq/badge.svg)](https://search.maven.org/artifact/top.arkstack/shine-mq/)
[![Latest release](https://img.shields.io/github/release/7le/shine-mq.svg)](https://github.com/7le/shine-mq/releases/latest)

English | [简体中文](./README-zh_CN.md)

### 🐣 Features

* **Seamless integration spring-boot-starter**
* **Encapsulate mq operation, easy to use**
* **Implement distributed transactions based on reliable message services (using AOP ideas and seamless integration with Spring, available through annotations)**
* **Reliable message default storage: redis (self-implementation)**
* **Current messaging middleware support : rabbitmq**

### 🐳 Maven

```
<dependency>
    <groupId>top.arkstack</groupId>
    <artifactId>shine-mq</artifactId>
    <version>2.1.0</version>
</dependency>
```
 
### 🎀 Distributed transaction

![shine-mq](https://github.com/7le/7le.github.io/raw/master/image/dis/shine-mq_EN.jpg)

### 🎐 blog

[Distributed transactions: based on reliable messaging services](https://7le.top/2018/12/04/%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1%EF%BC%9A%E5%9F%BA%E4%BA%8E%E5%8F%AF%E9%9D%A0%E6%B6%88%E6%81%AF%E6%9C%8D%E5%8A%A1/#more)

[Distributed transaction: reliable message delivery](https://7le.top/2019/02/21/%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1%EF%BC%9A%E6%B6%88%E6%81%AF%E5%8F%AF%E9%9D%A0%E5%8F%91%E9%80%81/)


### 🐹 Demo

Demo click [shine-mq-demo](https://github.com/7le/shine-mq-demo)

### 🌈 Configuration

Distributed transaction configuration, the specific configurable parameters are as follows：

```java
    /**
     * Whether to initialize open distributed transaction defaults to false.
     */
    private boolean transaction = false;

    /**
     * Submit ack failed maximum retries.
     */
     
    private Integer commitMaxRetries = 3;

    /**
     * Receive message ack failed maximum attempts.
     */
    private Integer receiveMaxRetries = 3;

    /**
     * Redis middleware is provided by default to implement persistence before messages are submitted to mq.
     *
     * Can achieve it by yourself {@link top.arkstack.shine.mq.coordinator.Coordinator}
     * Or don't want to use redis, you can set it to false, there will be no redis dependencies.
     */
    private boolean redisPersistence = true;
    
    /**
     * Redis cache prefix
     */
    private String redisPrefix = "";
    
    /**
     * Prepare and Ready status message timeouts default to 3 minutes (in seconds).
     */
    private long timeOut = 3 * 60;

    /**
     * The status expiration time of returnCallback defaults to 1 day (in seconds).
     */
    private long returnCallbackTTL = 24 * 60 * 60;

```

The operation of encapsulating mq, the specific configurable parameters are as follows：

```java
    /**
     * Whether to initialize the message listener, if the service is only a Producer, then close
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
     * The number of unconfirmed messages that each consumer may not complete.
     */
    private Integer prefetchCount = null;

    /**
     * Number of consumers created for each configured queue.
     */
    private Integer consumersPerQueue = null;

    /**
     * Whether it is persistent, whether it is saved to the erlang database mnesia, 
     * that is, whether the restart service disappears.
     */
    private boolean durable = true;

    /**
     * Whether it is exclusive, the currently defined queue is shared by the channel in the connection,
     * and other connection connections are not accessible.
     */
    private boolean exclusive = false;

    /**
     * Whether to delete automatically, refers to the queue delete when connection.close.
     */
    private boolean autoDelete = false;

    /**
     * Whether to initialize the message listener, if the service is only a Producer, then close
     */
    private boolean listenerEnable = false;

    /**
     * Channel cache
     */
    private Integer channelCacheSize = null;
```

### :octocat: End

> If it helps you, then help me with a star. ^.^