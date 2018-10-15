package top.arkstack.shine.mq.annotation;

import top.arkstack.shine.mq.constant.MqConstant;

import java.lang.annotation.*;

/**
 * 分布式事务 注解
 *
 * @author 7le
 * @version v1.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DistributedTrans {

    /**
     *  交换机
     */
    String exchange() default MqConstant.DISTUBUTED_TRANSCATION_EXCHANGE;

    /**
     * 路由key
     */
    String routeKey() default MqConstant.DISTUBUTED_TRANSCATION_ROUTEKEY;

    /**
     * 业务id
     */
    String bizId() default MqConstant.DISTUBUTED_TRANSCATION_BIZID;

    /**
     * 持久化方式c
     */
    String coordinator() default MqConstant.DISTUBUTED_TRANSCATION_COORDINATOR;
}
