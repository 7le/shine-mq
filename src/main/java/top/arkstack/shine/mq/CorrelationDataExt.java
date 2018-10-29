package top.arkstack.shine.mq;

import lombok.Data;
import org.springframework.amqp.rabbit.support.CorrelationData;
import top.arkstack.shine.mq.bean.EventMessage;

/**
 * 扩展 CorrelationData
 *
 * @author heqian7
 * @version 2.0.0
 */
@Data
public class CorrelationDataExt extends CorrelationData {

    private String coordinator;

    private Integer maxRetries;

    private EventMessage message;

    public CorrelationDataExt(String id, String coordinator, Integer maxRetries, EventMessage message) {
        super(id);
        this.coordinator = coordinator;
        this.maxRetries = maxRetries;
        this.message = message;
    }
}
