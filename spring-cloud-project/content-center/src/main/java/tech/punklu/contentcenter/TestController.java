package tech.punklu.contentcenter;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.punklu.contentcenter.dao.content.ShareMapper;
import tech.punklu.contentcenter.domain.dto.user.UserDTO;
import tech.punklu.contentcenter.domain.entity.content.Share;
import tech.punklu.contentcenter.feignclient.TestBaiduFeignClient;
import tech.punklu.contentcenter.feignclient.UserCenterFeignClient;

import java.util.ArrayList;
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




    @Autowired
    private UserCenterFeignClient userCenterFeignClient;

    @GetMapping("/testFeignParam")
    public UserDTO testFeignParam(UserDTO userDTO){
        return this.userCenterFeignClient.testFeignParam(userDTO);
    }

    @Autowired
    private TestBaiduFeignClient testBaiduFeignClient;

    @GetMapping("/baidu")
    public String baidu(){
        return this.testBaiduFeignClient.index();
    }


    @Autowired
    private TestService testService;

    @GetMapping("/test-a")
    public String testA(){
        this.testService.common();
        return "test-a";
    }

    @GetMapping("/test-b")
    public String testB(){
        this.testService.common();
        return "test-b";
    }

    @GetMapping("/test-hot")
    @SentinelResource("hot")
    public String testHot(@RequestParam(required = false)String a,
                          @RequestParam(required = false)String b){
        return a + " " + b;
    }

    @GetMapping("/test-add-flow-rule")
    public String testAddFlowRule(){
        this.initFlowQpsRule();
        return "success";
    }
    
    private void initFlowQpsRule(){
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule("/shares/1");
        // 设置QPS阈值
        rule.setCount(20);
        // 设置阈值类型
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setLimitApp("default");
        rules.add(rule);
        FlowRuleManager.loadRules(rules);

    }
}
