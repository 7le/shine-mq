package top.arkstack.shine.mq;

import org.springframework.amqp.rabbit.support.CorrelationData;

/**
 * 扩展 CorrelationData
 *
 * @author heqian7
 * @version 1.1.0
 */
public class CorrelationDataExt extends CorrelationData {

    private String coordinator;

    public CorrelationDataExt(String id, String coordinator){
        super(id);
        this.coordinator = coordinator;
    }

    public String getCoordinator(){
        return this.coordinator;
    }
}
