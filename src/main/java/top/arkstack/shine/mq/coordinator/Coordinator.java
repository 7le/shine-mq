package top.arkstack.shine.mq.coordinator;

import org.springframework.amqp.rabbit.support.CorrelationData;
import top.arkstack.shine.mq.bean.EventMessage;
import top.arkstack.shine.mq.bean.PrepareMessage;

import java.util.List;

/**
 * 协调者
 *
 * @author 7le
 * @version 2.0.0
 */
public interface Coordinator {

    /**
     * 设置消息为prepare状态
     * 用于回查时，定位执行的任务
     *
     * @param prepare prepare信息
     */
    void setPrepare(PrepareMessage prepare);

    /**
     * 补偿prepare状态消息
     *
     * @param prepare prepare信息
     * @throws Exception
     */
    void compensatePrepare(PrepareMessage prepare) throws Exception;

    /**
     * 设置消息为ready状态，删除prepare状态
     *
     * @param msgId       消息id
     * @param checkBackId 回查id
     * @param message     消息
     */
    void setReady(String msgId, String checkBackId, EventMessage message);

    /**
     * 删除状态（prepare状态）
     *
     * @param checkBackId 回查id
     */
    void delPrepare(String checkBackId);

    /**
     * 删除状态（ready状态）
     *
     * @param msgId 消息id
     */
    void delReady(String msgId);

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
    List<PrepareMessage> getPrepare() throws Exception;

    /**
     * 获取ready状态消息
     *
     * @return
     * @throws Exception
     */
    List<EventMessage> getReady() throws Exception;

    /**
     * 设置消息发送到RabbitMQ交换器，但无相应queue时的状态
     */
    void setReturnCallback(String msgId);

    /**
     * 获取ReturnCallback的状态
     */
    boolean getReturnCallback(String msgId);

    /**
     * 删除ReturnCallback的状态
     *
     * @param msgId 消息id
     */
    void delReturnCallback(String msgId);

    /**
     * 补偿ready状态消息
     *
     * @param message 消息
     * @throws Exception
     */
    void compensateReady(EventMessage message) throws Exception;

    /**
     * 删除回查后的状态 (prepare)
     *
     * @param ids 回查id
     */
    void delCheckBackIdWithPrepare(List<String> ids);

    /**
     * 删除回查后的状态 (ready)
     *
     * @param ids 回查id
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
