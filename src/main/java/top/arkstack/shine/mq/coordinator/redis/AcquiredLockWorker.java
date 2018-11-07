package top.arkstack.shine.mq.coordinator.redis;

/**
 * 获取锁后需要处理的逻辑
 *
 * @author 7le
 * @version 2.0.0
 */
@FunctionalInterface
public interface AcquiredLockWorker<T> {

    T invokeAfterLockAcquire() throws Exception;
}
