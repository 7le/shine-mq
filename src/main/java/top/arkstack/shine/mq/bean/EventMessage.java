package top.arkstack.shine.mq.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息类
 *
 * @author 7le
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage implements Serializable {

    private static final long serialVersionUID = -9203358002484642594L;

    private String exchangeName;

    private String routingKey;

    private String sendTypeEnum;

    private Object msg;

    private String coordinator;

    private String messageId;
}
