package tech.punklu.contentcenter.service.content;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.punklu.contentcenter.dao.content.ShareMapper;
import tech.punklu.contentcenter.domain.dto.content.ShareDTO;
import tech.punklu.contentcenter.domain.dto.user.UserDTO;
import tech.punklu.contentcenter.domain.entity.content.Share;

import java.util.List;

@Service
@Slf4j
public class ShareService {


    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 根据内容id获取分享内容的详情
     * @param id
     * @return
     */
    public ShareDTO findById(Integer id){
        // 获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        // 获取发布人id
        Integer userId = share.getUserId();
        // 获取用户中心所有实例的信息
        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
        String targetUrl = instances.stream().map(instance->instance.getUri().toString() + "/users/{id}").findFirst().orElseThrow(()->new IllegalArgumentException("当前没有用户中心实例"));
        log.info("请求的目标地址:{}",targetUrl);
        // 调用用户微服务查询对应的用户信息
        UserDTO userDTO = restTemplate.getForObject(targetUrl,UserDTO.class,userId);
        // 消息装备
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share,shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }
}
