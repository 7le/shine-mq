package top.arkstack.shine.mq.processor;

/**
 * 普通模式
 *
 * @author 7le
 * @version 1.0.0
 */
public abstract class BaseProcessor implements Processor {

    /**
     * 执行之前被调用
     */
    protected void beforeRun(Object t) {
    }

    /**
     * 执行之后被调用
     */
    protected void afterRun(Object t) {
    }
}
