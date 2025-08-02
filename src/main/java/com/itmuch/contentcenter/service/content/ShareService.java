package com.itmuch.contentcenter.service.content;

import com.itmuch.contentcenter.dao.content.ShareMapper;
import com.itmuch.contentcenter.dao.messaging.RocketmqTransactionLogMapper;
import com.itmuch.contentcenter.domain.dto.content.AuditDTO;
import com.itmuch.contentcenter.domain.dto.content.ShareDTO;
import com.itmuch.contentcenter.domain.dto.messaging.UserAddBonusMessageDTO;
import com.itmuch.contentcenter.domain.dto.user.UserDTO;
import com.itmuch.contentcenter.domain.entity.content.Share;
import com.itmuch.contentcenter.domain.entity.messaging.RocketmqTransactionLog;
import com.itmuch.contentcenter.domain.enums.AuditStatusEnum;
import com.itmuch.contentcenter.feignclient.UserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {

    @Autowired
    private final ShareMapper shareMapper;

    @Autowired
    private final RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final DiscoveryClient discoveryClient;

    @Autowired
    private final UserCenterFeignClient userCenterFeignClient;

    @Autowired
    private final RocketMQTemplate rocketMQTemplate;

    public ShareDTO findById(Integer id) {
        // 获取分享详情
        Share share = shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();

        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
//        String targetUrl = instances.stream()
//                .map(x -> x.getUri().toString() + "/users/{userId}")
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("没有找到对应的用户中心服务！"));

//        List<String> targetURLs = instances.stream()
//                .map(x -> x.getUri().toString() + "/users/{userId}")
//                .collect(Collectors.toList());
//
//        int i = ThreadLocalRandom.current().nextInt(targetURLs.size());
//
//        userId = 1;
//        String targetURL = targetURLs.get(i);
//        log.info("请求的目标地址为：[{}]", targetURL);
//        // "http://localhost:8080/users/{userId}"
//        UserDTO userDTO = restTemplate.getForObject(
//                targetURL,
//                UserDTO.class,
//                userId);

//        userId = 1;
//        UserDTO userDTO = restTemplate.getForObject(
//                "http://user-center/users/{userId}",
//                UserDTO.class,
//                userId);

        UserDTO userDTO = this.userCenterFeignClient.findByUserId(userId);

        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());

        return shareDTO;
    }

    public static void main(String[] args) {

    }

    public Share auditById(Integer id, AuditDTO auditDTO) {
        Share share = shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("审核操作失败！不存在投稿信息！");
        }

        if (!"NOT_YET".equals(share.getAuditStatus())) {
            throw new IllegalArgumentException("审核操作失败！投稿信息已完成审核！");
        }

        if (AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())) {
            // 发送事务消息
            String transactionId = UUID.randomUUID().toString();
            rocketMQTemplate.sendMessageInTransaction("tx-add-bonus-group",
                    "add-bonus",
                    MessageBuilder.withPayload(
                                new UserAddBonusMessageDTO().builder()
                                        .userId(share.getUserId())
                                        .bonus(50)
                                        .build()
                            )
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("share_id", id)
                            .build(),
                    auditDTO);
        } else if (AuditStatusEnum.REJECT.equals(auditDTO.getAuditStatusEnum())) {
            auditByIdInDB(id, auditDTO);
        }

        return share;
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, AuditDTO auditDTO) {
        Share share = Share.builder()
                           .id(id)
                           .auditStatus(auditDTO.getAuditStatusEnum().toString())
                           .reason(auditDTO.getReason())
                           .build();

        shareMapper.updateByPrimaryKeySelective(share);
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDBWithRocketMqLog(Integer id, AuditDTO auditDTO, String transactionId) {
        this.auditByIdInDB(id, auditDTO);

        // 记录一下日志
        rocketmqTransactionLogMapper
                .insertSelective(
                        RocketmqTransactionLog.builder()
                                .transactionId(transactionId)
                                .log("记录一下审核分享。。。。。。")
                                .build()
                );
    }

}
