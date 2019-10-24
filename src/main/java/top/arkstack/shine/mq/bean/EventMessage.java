package top.arkstack.shine.mq.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息类
 *
 * @author 7le
 * @version 1.0.0
 */
@Data
public class EventMessage implements Serializable {

    private static final long serialVersionUID = -9203358002484642594L;

    private String exchangeName;

    private String routingKey;

    private String sendTypeEnum;

    private Object data;

    private String coordinator;

    private String messageId;

    private String rollback;

    public EventMessage(String exchangeName, String routingKey, String sendTypeEnum, Object data, String coordinator, String messageId, String rollback) {
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.sendTypeEnum = sendTypeEnum;
        this.data = data;
        this.coordinator = coordinator;
        this.messageId = messageId;
        this.rollback = rollback;
    }

    public EventMessage(String exchangeName, String routingKey, String sendTypeEnum, Object data, String coordinator, String messageId) {
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.sendTypeEnum = sendTypeEnum;
        this.data = data;
        this.coordinator = coordinator;
        this.messageId = messageId;
    }
}
