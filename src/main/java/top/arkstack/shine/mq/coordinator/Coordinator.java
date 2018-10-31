package top.arkstack.shine.mq.coordinator;

import top.arkstack.shine.mq.bean.EventMessage;
import java.util.List;

/**
 * @author 7le
 * @version 2.0.0
 */
public interface Coordinator {

    /**
     * 设置消息为prepare状态
     *
     * @param msgId 消息id
     */
    void setPrepare(String msgId);

    /**
     * 设置消息为ready状态，删除prepare状态
     *
     * @param msgId 消息id
     * @param message 消息
     */
    void setReady(String msgId, EventMessage message);

    /**
     * 删除状态
     *
     * @param msgId 消息id
     */
    void delStatus(String msgId);

    /**
     * 设置消息为 失败待捞起重试消息
     *
     * @param msgId 消息id
     */
    void setRetry(String msgId);

    /**
     * 获取消息
     *
     * @param msgId 消息id
     * @return 消息
     */
    EventMessage getMetaMsg(String msgId);

    /**
     * 获取ready状态消息
     *
     * @return
     * @throws Exception
     */
    List getReady() throws Exception;

    /**
     * 获取prepare状态消息
     *
     * @return
     * @throws Exception
     */
    List getPrepare() throws Exception;

    /**
     * 消息重发次数+1
     *
     * @param key 重发消息key
     * @param hashKey 消息id
     * @return 消息重发次数
     */
    Long incrementResendKey(String key, String hashKey);

    /**
     * 获取重发消息的值（消息重发次数）
     *
     * @param key 消息id
     * @param hashKey 消息id
     * @return 消息重发次数
     */
    Long getResendValue(String key, String hashKey);

    /**
     * 删除重发消息的值
     *
     * @param key 重发消息key
     * @param hashKey 消息id
     */
    void delResendKey(String key,String hashKey);

}
