package top.arkstack.shine.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

/**
 * 异常处理
 *
 * @author 7le
 * @version 1.0.0
 */
public class MessageErrorHandler implements ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        logger.error("MQ happen a error:" + t.getMessage(), t);
    }

}
