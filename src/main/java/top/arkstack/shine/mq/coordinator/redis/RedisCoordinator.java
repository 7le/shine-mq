package top.arkstack.shine.mq.coordinator.redis;

import org.springframework.beans.factory.annotation.Autowired;
import top.arkstack.shine.mq.bean.EventMessage;
import top.arkstack.shine.mq.constant.MqConstant;
import top.arkstack.shine.mq.coordinator.Coordinator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 提供基于redis实现
 *
 * @author 7le
 * @version 2.0.0
 */
public class RedisCoordinator implements Coordinator {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void setPrepare(String checkBackId) {
        redisUtil.hset(MqConstant.DISTRIBUTED_MSG_PREPARE, checkBackId,
                checkBackId + MqConstant.SPLIT + System.currentTimeMillis());
    }

    @Override
    public void setReady(String msgId, String checkBackId, EventMessage message) {
        redisUtil.hset(MqConstant.DISTRIBUTED_MSG_READY, msgId, message);
        redisUtil.hdel(MqConstant.DISTRIBUTED_MSG_PREPARE, checkBackId);
    }

    @Override
    public void delStatus(String msgId) {
        redisUtil.hdel(MqConstant.DISTRIBUTED_MSG_READY, msgId);
    }

    @Override
    public EventMessage getMetaMsg(String msgId) {
        return (EventMessage) redisUtil.hget(MqConstant.DISTRIBUTED_MSG_READY, msgId);
    }

    @Override
    public List getPrepare() throws Exception {
        List<Object> values = redisUtil.hvalues(MqConstant.DISTRIBUTED_MSG_PREPARE);
        List<String> keys = new ArrayList<>();
        for (Object value : values) {
            if (msgTimeOut(value.toString())) {
                String key = (value.toString().split(MqConstant.SPLIT))[0];
                keys.add(key);
            }
        }
        return keys;
    }

    @Override
    public List getReady() throws Exception {
        List<Object> messages = redisUtil.hvalues(MqConstant.DISTRIBUTED_MSG_READY);
        List<EventMessage> messageAlert = new ArrayList();
        for (Object o : messages) {
            EventMessage m = (EventMessage) o;
            if (msgTimeOut(m.getMessageId())) {
                messageAlert.add(m);
            }
        }
        return messageAlert;
    }

    @Override
    public void delCheckBackIdWithPrepare(List<String> ids) {
        redisUtil.hdel(MqConstant.DISTRIBUTED_MSG_PREPARE, ids.toArray());
    }

    @Override
    public void delCheckBackIdWithReady(List<EventMessage> ids) {
        List<String> messageIds = new ArrayList<>();
        ids.forEach(s -> messageIds.add(s.getMessageId()));
        redisUtil.hdel(MqConstant.DISTRIBUTED_MSG_READY, ids.toArray());
    }

    @Override
    public Double incrementResendKey(String key, String hashKey) {
        return redisUtil.hincr(key, hashKey, 1);
    }

    @Override
    public Double getResendValue(String key, String hashKey) {
        return (Double) redisUtil.hget(key, hashKey);
    }

    @Override
    public void delResendKey(String key, String hashKey) {
        redisUtil.del(key, hashKey);
    }

    private boolean msgTimeOut(String messageId) throws Exception {
        String time = (messageId.split(MqConstant.SPLIT))[1];
        long timeGap = System.currentTimeMillis() - Long.parseLong(time);
        if (timeGap > MqConstant.TIME_OUT) {
            return true;
        }
        return false;
    }
}
