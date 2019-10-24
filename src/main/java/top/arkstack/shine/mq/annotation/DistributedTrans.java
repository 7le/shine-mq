package top.arkstack.shine.mq.annotation;

import top.arkstack.shine.mq.constant.MqConstant;

import java.lang.annotation.*;

/**
 * 分布式事务 注解
 *
 * @author 7le
 * @version 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DistributedTrans {

    /**
     * 交换机
     */
    String exchange() default MqConstant.DISTRIBUTED_TRANSACTION_EXCHANGE;

    /**
     * 路由key
     */
    String routeKey() default MqConstant.DISTRIBUTED_TRANSACTION_ROUTEKEY;

    /**
     * 业务id
     * 需要自定义，以防重复
     */
    String bizId() default MqConstant.DISTRIBUTED_TRANSACTION_BIZID;

    /**
     * 持久化方式
     */
    String coordinator() default MqConstant.DISTRIBUTED_TRANSACTION_COORDINATOR;

    /**
     * 回滚地址
     */
    String rollback() default "";
}
