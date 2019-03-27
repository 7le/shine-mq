package top.arkstack.shine.mq.bean;

import lombok.*;

import java.io.Serializable;

/**
 * Prepare状态 消息类
 *
 * @author 7le
 * @version 2.0.0
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class PrepareMessage implements Serializable {

    private static final long serialVersionUID = -7800330699860517306L;

    /**
     * 回查id
     */
    @NonNull
    private String checkBackId;

    /**
     * 业务标识
     */
    @NonNull
    private String bizId;

    /**
     * 交换机
     */
    @NonNull
    private String exchangeName;

    /**
     * 路由密钥
     */
    @NonNull
    private String routingKey;

    /**
     * 要发送的消息内容，在setPrepare是不需要的
     */
    private Object data;
}
