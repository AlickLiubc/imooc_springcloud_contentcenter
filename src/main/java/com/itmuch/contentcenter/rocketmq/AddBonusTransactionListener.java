package com.itmuch.contentcenter.rocketmq;

import com.itmuch.contentcenter.dao.messaging.RocketmqTransactionLogMapper;
import com.itmuch.contentcenter.domain.dto.content.AuditDTO;
import com.itmuch.contentcenter.domain.entity.messaging.RocketmqTransactionLog;
import com.itmuch.contentcenter.service.content.ShareService;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

@RocketMQTransactionListener(txProducerGroup = "tx-add-bonus-group")
public class AddBonusTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired(required = false)
    private ShareService shareService;

    @Autowired(required = false)
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        MessageHeaders headers = message.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        Integer shareId = Integer.valueOf((String) headers.get("share_id"));

        AuditDTO auditDTO = (AuditDTO)o;

        try {
            shareService.auditByIdInDBWithRocketMqLog(shareId, auditDTO, transactionId);
        } catch (Exception ex) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        MessageHeaders headers = message.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);

        RocketmqTransactionLog rocketmqTransactionLog = null;

        try {
            rocketmqTransactionLog = rocketmqTransactionLogMapper.selectOne(
                                            RocketmqTransactionLog
                                            .builder()
                                            .transactionId(transactionId)
                                            .build());

        } catch (Exception ex) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        if (rocketmqTransactionLog != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }

        return RocketMQLocalTransactionState.ROLLBACK;
    }

}
