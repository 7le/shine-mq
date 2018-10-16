package top.arkstack.shine.mq.coordinator;

import top.arkstack.shine.mq.bean.EventMessage;
import java.util.List;

public interface Coordinator {

    /**
     * 设置消息为prepare状态
     */
    void setPrepare(String msgId);

    /**
     * 设置消息为ready状态，删除prepare状态
     */
    void setReady(String msgId, EventMessage rabbitMetaMessage);

    /**
     * 消息发送成功，删除ready状态消息
     */
    void setSuccess(String msgId);

    /**
     * 获取消息
     */
    EventMessage getMetaMsg(String msgId);

    /**
     * 获取ready状态消息
     */
    List getReady() throws Exception;

    /**
     * 获取prepare状态消息
     */
    List getPrepare() throws Exception;

    /**
     * 消息重发次数+1
     */
    Long incrementResendKey(String key, String hashKey);

    /**
     * 获取重发消息的值（消息重发次数）
     */
    Long getResendValue(String key, String hashKey);

}
