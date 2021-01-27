package tech.punklu.contentcenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.punklu.contentcenter.dao.content.ShareMapper;
import tech.punklu.contentcenter.domain.entity.content.Share;

import java.util.Date;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private ShareMapper shareMapper;

    @GetMapping("/test")
    public List<Share> testInsert(){
        Share share = new Share();
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setTitle("xxx");
        share.setCover("xxx");
        share.setAuthor("punklu");
        share.setBuyCount(1);

        this.shareMapper.insertSelective(share);

        return this.shareMapper.selectAll();
    }

    /**
     * DiscoveryClient对于Nacos、Zookeeper、eureka都适用
     */
    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 测试服务发现，证明内容中心可以找到用户中心
     * @return 用户中心所有实例的地址信息
     */
    @GetMapping("/getInstances")
    public List<ServiceInstance> getInstances(){
        // 查询指定服务的所有实例的信息
        return this.discoveryClient.getInstances("user-center");
    }
}
