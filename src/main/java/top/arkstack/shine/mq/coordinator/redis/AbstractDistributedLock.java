package top.arkstack.shine.mq.coordinator.redis;

/**
 * @author 7le
 * @version 2.0.0
 */
public abstract class AbstractDistributedLock implements DistributedLocker {

    @Override
    public <T> T lock(String key, AcquiredLockWorker<T> worker) throws Exception {
        return lock(key, TIMEOUT_MILLIS, RETRY_TIMES, SLEEP_MILLIS, worker);
    }

    @Override
    public <T> T lock(String key, int retryTimes, AcquiredLockWorker<T> worker) throws Exception {
        return lock(key, TIMEOUT_MILLIS, retryTimes, SLEEP_MILLIS, worker);
    }

    @Override
    public <T> T lock(String key, int retryTimes, long sleepMillis, AcquiredLockWorker<T> worker) throws Exception {
        return lock(key, TIMEOUT_MILLIS, retryTimes, sleepMillis, worker);
    }

    @Override
    public <T> T lock(String key, long expire, AcquiredLockWorker<T> worker) throws Exception {
        return lock(key, expire, RETRY_TIMES, SLEEP_MILLIS, worker);
    }

    @Override
    public <T> T lock(String key, long expire, int retryTimes, AcquiredLockWorker<T> worker) throws Exception {
        return lock(key, expire, retryTimes, SLEEP_MILLIS, worker);
    }

}
