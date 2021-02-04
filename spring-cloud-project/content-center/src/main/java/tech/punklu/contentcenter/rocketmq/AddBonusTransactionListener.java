package tech.punklu.contentcenter.rocketmq;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import tech.punklu.contentcenter.dao.message.RocketmqTransactionLogMapper;
import tech.punklu.contentcenter.domain.dto.content.ShareAuditDTO;
import tech.punklu.contentcenter.domain.dto.message.RocketmqTransactionLog;
import tech.punklu.contentcenter.service.content.ShareService;

/**
 * 用于对添加积分功能实现RocketMQ调用内容中心：
 * 1、执行本地事务
 * 2、检查本地事务执行状态
 */
@RocketMQTransactionListener
public class AddBonusTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired
    private ShareService shareService;

    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    /**
     * 执行本地事务
     * @param message 生产者（内容中心发送的半消息）
     * @param o 生产发送半消息时和半消息一起发送的参数对象
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        MessageHeaders headers = message.getHeaders();
        // 获取之前生产者（内容中心）发送半消息时指定的分布式事务id,确定本次的事务
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        // 获取之前生产者（内容中心）发送半消息时放在header中的投稿id
        Integer shareId = Integer.valueOf((String)headers.get("share_id"));

        // 尝试执行生产者（内容中心）的本地事务
        try{
            this.shareService.auditByIdWithRocketMqLog(shareId, (ShareAuditDTO) o,transactionId);
            // 有可能失败的地方，可能在执行完本地事务后，还没来得及告诉RocketMQ本地事务已完成，
            // 生产者就挂掉了，或者网络暂时不通了，所以需要
            // 有下面的RocketMQ回查生产者本地事务状态的接口(checkLocalTransaction)
            return RocketMQLocalTransactionState.COMMIT;
        }catch (Exception e){
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * rocketMQ回查本地事务是否执行成功的接口
     * @param message
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        MessageHeaders headers = message.getHeaders();
        String transactionId = (String)headers.get(RocketMQHeaders.TRANSACTION_ID);
        // 根据生产者发送半消息时指定的事务id从数据库查询这个事务的执行记录
        RocketmqTransactionLog transactionLog = this.rocketmqTransactionLogMapper.selectOne(
            RocketmqTransactionLog.builder()
                    .transactionId(transactionId)
                    .build()
        );
        // 如果查询到了这条记录，说明本地事务已执行成功，否则，说明执行失败,通过RocketMQ回滚这条消息
        if (transactionLog != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
