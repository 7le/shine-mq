package top.arkstack.shine.mq.processor;

/**
 * 消费接口
 *
 * @author 7le
 * @version 1.0.0
 */
public interface Processor {

    Object process(Object e);
}
