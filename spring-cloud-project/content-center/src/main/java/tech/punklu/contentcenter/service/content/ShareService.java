package tech.punklu.contentcenter.service.content;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tech.punklu.contentcenter.dao.content.ShareMapper;
import tech.punklu.contentcenter.domain.dto.content.ShareAuditDTO;
import tech.punklu.contentcenter.domain.dto.content.ShareDTO;
import tech.punklu.contentcenter.domain.dto.message.UserAddBonusMsgDTO;
import tech.punklu.contentcenter.domain.dto.user.UserDTO;
import tech.punklu.contentcenter.domain.entity.content.Share;
import tech.punklu.contentcenter.feignclient.UserCenterFeignClient;

import java.util.List;
import java.util.Objects;

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
        // 审核资源，将状态设为PASS/REJECT
        share.setAuditStatus(auditDTO.getAuditStatusEnum().toString());
        this.shareMapper.updateByPrimaryKey(share);

        // 如果是PASS，那么发送消息给rocketMQ，让用户中心去消费，并为发布人添加积分
        // 异步执行，缩短响应耗时
        this.rocketMQTemplate.convertAndSend("add-bonus",
                UserAddBonusMsgDTO.builder()
                .userId(share.getUserId())
                .bonus(50).build()
        );
        return share;
    }
}
