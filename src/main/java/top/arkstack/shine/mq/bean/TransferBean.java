package top.arkstack.shine.mq.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分布式事务消息的传输类
 *
 * @author 7le
 * @version 2.0.3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferBean {

    /**
     * 回查id
     */
    private String checkBackId;

    /**
     * 需要传输的数据
     */
    private Object data;
}
