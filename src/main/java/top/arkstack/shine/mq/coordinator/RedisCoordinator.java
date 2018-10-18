package top.arkstack.shine.mq.coordinator;

import top.arkstack.shine.mq.bean.EventMessage;

import java.util.List;

/**
 * 提供基于redis实现
 */
public class RedisCoordinator implements Coordinator{

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
}
