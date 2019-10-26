package top.arkstack.shine.mq.constant;

/**
 * mq 常量
 *
 * @author 7le
 * @version 2.0.0
 */
public class MqConstant {

    public static final String DISTRIBUTED_TRANSACTION_EXCHANGE = "distributed_transaction_exchange";

    public static final String DISTRIBUTED_TRANSACTION_ROUTEKEY = "distributed_transaction_routekey";

    public static final String DISTRIBUTED_TRANSACTION_BIZID = "distributed_transaction_bizid";

    public static final String DISTRIBUTED_TRANSACTION_COORDINATOR = "redisCoordinator";

    public static final String SPLIT = "_";

    public static final String TIME = "yyyy-MM-dd HH:mm:ss";

    public static final String DATA_DEFAULT = "";

    public static final String RECEIVE_RETRIES = "receive_retries";

    public static final String DEAD_LETTER_QUEUE = "dead_letter_queue";

    public static final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";

    public static final String DEAD_LETTER_ROUTEKEY = "dead_letter_routekey";

    public static final String DISTRIBUTED_MSG_READY = "distributed_msg_ready";

    public static final String DISTRIBUTED_MSG_PREPARE = "distributed_msg_prepare";

    public static final String DISTRIBUTED_MSG_ROLLBACK = "distributed_msg_rollback";

    public static final String RETURN_CALLBACK="return_callback_";
}
