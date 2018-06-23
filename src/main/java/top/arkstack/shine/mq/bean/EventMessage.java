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
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage implements Serializable {

    private static final long serialVersionUID = -9203358002484642594L;

    private String queueName;

    private String exchangeName;

    private String routingKey;

    private Object data;
}
