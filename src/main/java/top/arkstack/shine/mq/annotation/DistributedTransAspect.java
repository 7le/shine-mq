package top.arkstack.shine.mq.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.arkstack.shine.mq.RabbitmqFactory;
import top.arkstack.shine.mq.ShineMqException;
import top.arkstack.shine.mq.bean.EventMessage;
import top.arkstack.shine.mq.bean.SendTypeEnum;
import top.arkstack.shine.mq.bean.TransferBean;
import top.arkstack.shine.mq.constant.MqConstant;
import top.arkstack.shine.mq.coordinator.Coordinator;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 分布式事务 {@link top.arkstack.shine.mq.annotation.DistributedTrans} 切面
 *
 * @author 7le
 * @version 2.0.0
 */
@Slf4j
@Aspect
@Order(-99)
@Component
public class DistributedTransAspect {

    @Autowired
    ApplicationContext context;

    @Autowired
    RabbitmqFactory rabbitmqFactory;

    private volatile boolean flag = true;


    @Around(value = "@annotation(trans)")
    public void around(ProceedingJoinPoint pjp, DistributedTrans trans) throws Throwable {
        if (!rabbitmqFactory.getConfig().getDistributed().isTransaction()) {
            throw new ShineMqException("Use distributed transaction, the transaction parameter must be true.");
        }

        log.info("Start distributed transaction : {} ", trans);
        String exchange = trans.exchange();
        String routeKey = trans.routeKey();
        String coordinatorName = trans.coordinator();
        // 防止多节点下同一事务 同一时间点下，msgId重复 增加时间戳和本机本地ip
        String msgId = trans.bizId() + MqConstant.SPLIT + System.currentTimeMillis() + MqConstant.SPLIT + getIpAddress();

        Coordinator coordinator;
        try {
            coordinator = (Coordinator) context.getBean(coordinatorName);
        } catch (Exception e) {
            log.error("No coordinator or not joined the spring container : ", e);
            throw e;
        }
        Object bean;
        try {
            bean = pjp.proceed();
        } catch (Exception e) {
            log.error("Biz execution failed, id : {} :", msgId, e);
            throw e;
        }
        if (!(bean instanceof TransferBean)) {
            throw new ShineMqException("Return value please use TransferBean");
        }
        TransferBean transferBean = (TransferBean) bean;
        if (transferBean.getCheckBackId() == null) {
            throw new ShineMqException("Check back id cannot be empty.");
        }
        try {
            EventMessage message = new EventMessage(exchange, routeKey, SendTypeEnum.DISTRIBUTED.toString(), transferBean,
                    coordinatorName, msgId);
            //将消息持久化
            coordinator.setReady(msgId, transferBean.getCheckBackId(), message);
            rabbitmqFactory.setCorrelationData(msgId, coordinatorName, message, null);
            rabbitmqFactory.addDLX(exchange, exchange, routeKey, null, null);
            if (flag) {
                rabbitmqFactory.add(MqConstant.DEAD_LETTER_QUEUE, MqConstant.DEAD_LETTER_EXCHANGE,
                        MqConstant.DEAD_LETTER_ROUTEKEY, null, null);
                flag = false;
            }
            rabbitmqFactory.getTemplate().send(message, 0, 0, SendTypeEnum.DISTRIBUTED);
        } catch (Exception e) {
            log.error("Message failed to be sent : ", e);
            throw e;
        }

    }

    private static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
