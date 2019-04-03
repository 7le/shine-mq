package top.arkstack.shine.mq;

/**
 * shine-mq 异常
 *
 * @author 7le
 * @version 2.0.0
 */
public class ShineMqException extends RuntimeException {

    private static final long serialVersionUID = 8936118899975856767L;

    public ShineMqException(String message) {
        super(message);
    }
}
