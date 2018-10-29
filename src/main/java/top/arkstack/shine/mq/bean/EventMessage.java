package top.arkstack.shine.mq.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 7le
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
public class EventMessage implements Serializable {

    private static final long serialVersionUID = -9203358002484642594L;

    private String exchangeName;

    private String routingKey;

    private String sendTypeEnum;

    private Object data;

    private String coordinator;

    public EventMessage(String exchangeName, String routingKey, String sendTypeEnum, Object data) {
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.sendTypeEnum = sendTypeEnum;
        this.data = data;
    }
}
