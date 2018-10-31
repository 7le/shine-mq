package top.arkstack.shine.mq.coordinator.redis;

import top.arkstack.shine.mq.bean.EventMessage;
import top.arkstack.shine.mq.coordinator.Coordinator;

import java.util.List;

/**
 * 提供基于redis实现
 *
 * @author 7le
 * @version 2.0.0
 */
public class RedisCoordinator implements Coordinator {

    @Override
    public void setPrepare(String msgId) {

    }

    @Override
    public void setReady(String msgId, EventMessage message) {

    }

    @Override
    public void delStatus(String msgId) {

    }

    @Override
    public void setRetry(String msgId) {

    }

    @Override
    public EventMessage getMetaMsg(String msgId) {
        return null;
    }

    @Override
    public List getReady() throws Exception {
        return null;
    }

    @Override
    public List getPrepare() throws Exception {
        return null;
    }

    @Override
    public Long incrementResendKey(String key, String hashKey) {
        return null;
    }

    @Override
    public Long getResendValue(String key, String hashKey) {
        return null;
    }

    @Override
    public void delResendKey(String key, String hashKey) {

    }
}
