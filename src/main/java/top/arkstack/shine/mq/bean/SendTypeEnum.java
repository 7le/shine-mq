package top.arkstack.shine.mq.bean;

/**
 * Fanout: 订阅模式
 * 任何发送到Fanout Exchange的消息都会被转发到与该Exchange绑定(Binding)的所有Queue上。
 * <p>
 * Direct: 路由模式
 * 任何发送到Direct Exchange的消息都会被转发到routingKey中指定的Queue。
 * <p>
 * Topic:  通配符模式
 * 基本思想和路由模式是一样的，只不过路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
 * <p>
 * RPC:    远程调用方法
 * <p>
 * Distributed: 自定义添加的分布式事务类型
 *
 * @author 7le
 * @version 1.0.0
 */
public enum SendTypeEnum {
    TOPIC, RPC, FANOUT, DIRECT, DISTRIBUTED, DLX
}
