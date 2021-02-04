package tech.punklu.contentcenter.service.content;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
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
import tech.punklu.contentcenter.dao.content.ShareMapper;
import tech.punklu.contentcenter.dao.message.RocketmqTransactionLogMapper;
import tech.punklu.contentcenter.domain.dto.content.ShareAuditDTO;
import tech.punklu.contentcenter.domain.dto.content.ShareDTO;
import tech.punklu.contentcenter.domain.dto.message.RocketmqTransactionLog;
import tech.punklu.contentcenter.domain.dto.message.UserAddBonusMsgDTO;
import tech.punklu.contentcenter.domain.dto.user.UserDTO;
import tech.punklu.contentcenter.domain.entity.content.Share;
import tech.punklu.contentcenter.domain.enums.AuditStatusEnum;
import tech.punklu.contentcenter.feignclient.UserCenterFeignClient;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class ShareService {


    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private UserCenterFeignClient userCenterFeignClient;

    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    /**
     * 根据内容id获取分享内容的详情
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ShareDTO findById(Integer id){
        // 获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        // 获取发布人id
        Integer userId = share.getUserId();

        // 从ribbon负载均衡中心获得用户中心的地址

        UserDTO userDTO = this.userCenterFeignClient.findById(userId);
        // 消息装备
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share,shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public Share auditById(Integer id, ShareAuditDTO auditDTO) {
        // 查询Share是否存在，不存在或者当前的audit_status != NOT_YET，那么抛异常
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null){
            throw new IllegalArgumentException("参数非法！该分享不存在！");
        }
        if (!Objects.equals("NOT_YET",share.getAuditStatus())){
            throw new IllegalArgumentException("参数非法！该分享已审核过");
        }
        // 如果是PASS，那么发送消息给rocketMQ，让用户中心去消费，并为发布人添加积分
        if(AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())){
            // 发送半消息（即在事务中发送消息）
            // UUID作为分布式事务id
            String transactionId = UUID.randomUUID().toString();
            this.rocketMQTemplate.sendMessageInTransaction(
                    "add-bonus",
                    MessageBuilder.
                            withPayload(UserAddBonusMsgDTO.builder()
                                    .userId(share.getUserId())
                                    .bonus(50)
                                    .build()
                            )
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("share_id", id)
                            .build(), auditDTO
            );
        }else {
            // 说明是要拒绝掉这个投稿，只需要将数据库中这个投稿的审核状态改为拒绝即可，
            // 不需要发送给用户增加积分的MQ消息
            this.auditById(id,auditDTO);
        }

        return share;
    }

    /**
     * 更新投稿消息审核状态的变化
     * @param id
     * @param auditDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id,ShareAuditDTO auditDTO) {
        Share share = Share.builder()
                .id(id)
                .auditStatus(auditDTO.getAuditStatusEnum().toString())
                .reason(auditDTO.getReason())
                .build();
        this.shareMapper.updateByPrimaryKeySelective(share);
    }

    /**
     * 先执行本地事务，再记录事务成功执行的信息在数据库表里，提供给RocketMQ回查
     * @param id
     * @param auditDTO
     * @param transactionId
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id,
                                         ShareAuditDTO auditDTO,
                                         String transactionId){
        // 更新数据库的投稿状态
        this.auditByIdInDB(id,auditDTO);
        // 写入本地事务执行成功的日志，用于后续RocketMQ回查确认
        this.rocketmqTransactionLogMapper.insertSelective(
                RocketmqTransactionLog.builder()
                .transactionId(transactionId)
                .log("审核分享...")
                .build()
        );
    }
}
