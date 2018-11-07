package top.arkstack.shine.mq.coordinator.redis;

/**
 * 不能获取锁的异常类
 *
 * @author 7le
 * @version 2.0.0
 */
public class UnableToAcquireLockException extends RuntimeException {

    public UnableToAcquireLockException() {
    }

    public UnableToAcquireLockException(String message) {
        super(message);
    }

    public UnableToAcquireLockException(String message, Throwable cause) {
        super(message, cause);
    }
}