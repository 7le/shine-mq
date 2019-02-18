package top.arkstack.shine.mq.coordinator;

import org.springframework.amqp.rabbit.support.CorrelationData;
import top.arkstack.shine.mq.bean.EventMessage;

import java.util.List;

/**
 * @author 7le
 * @version 2.0.0
 */
public interface Coordinator {

    /**
     * 设置消息为prepare状态
     * 用于回查时，定位执行的任务
     *
     * @param checkBackId 回查id
     */
    void setPrepare(String checkBackId);

    /**
     * 设置消息为ready状态，删除prepare状态
     *
     * @param msgId         消息id
     * @param checkBackId   回查id
     * @param message       消息
     */
    void setReady(String msgId, String checkBackId, EventMessage message);

    /**
     * 删除状态（ready状态）
     *
     * @param msgId 消息id
     */
    void delStatus(String msgId);

    /**
     * 获取消息
     *
     * @param msgId 消息id
     * @return 消息
     */
    EventMessage getMetaMsg(String msgId);


    /**
     * 获取prepare状态消息
     *
     * @return
     * @throws Exception
     */
    List getPrepare() throws Exception;

    /**
     * 获取ready状态消息
     *
     * @return
     * @throws Exception
     */
    List getReady() throws Exception;

    /**
     * 删除回查后的状态 (prepare)
     *
     * @param ids   回查id
     */
    void delCheckBackIdWithPrepare(List<String> ids);

    /**
     * 删除回查后的状态 (ready)
     *
     * @param ids   回查id
     */
    void delCheckBackIdWithReady(List<EventMessage> ids);

    /**
     * 消息重发次数+1
     *
     * @param key     重发消息key
     * @param hashKey 消息id
     * @return 消息重发次数
     */
    Double incrementResendKey(String key, String hashKey);

    /**
     * 获取重发消息的值（消息重发次数）
     *
     * @param key     消息id
     * @param hashKey 消息id
     * @return 消息重发次数
     */
    Double getResendValue(String key, String hashKey);

    /**
     * 删除重发消息的值
     *
     * @param key     重发消息key
     * @param hashKey 消息id
     */
    void delResendKey(String key, String hashKey);

    /**
     * 消息发送到RabbitMQ后 自定义ack回调
     *
     * @param correlationData
     * @param ack
     */
    void confirmCallback(CorrelationData correlationData, boolean ack);
}
