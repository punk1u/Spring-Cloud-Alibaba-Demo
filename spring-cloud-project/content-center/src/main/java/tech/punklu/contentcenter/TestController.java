package tech.punklu.contentcenter;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import tech.punklu.contentcenter.dao.content.ShareMapper;
import tech.punklu.contentcenter.domain.dto.user.UserDTO;
import tech.punklu.contentcenter.domain.entity.content.Share;
import tech.punklu.contentcenter.feignclient.TestBaiduFeignClient;
import tech.punklu.contentcenter.feignclient.UserCenterFeignClient;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
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

    @GetMapping("/test-sentinel-api")
    public String testSentinelAPI(@RequestParam(required = false) String a){

        String resourceName = "test-sentinel-api";
        ContextUtil.enter(resourceName,"test-service");

        // 定义一个sentinel保护的资源
        Entry entry = null;
        try {
            entry = SphU.entry("test-sentinel-api");
            // 被保护的业务逻辑
            if (StringUtils.isBlank(a)){
                throw new IllegalArgumentException("a不能为空");
            }
            return a;
        }
        // 如果被保护的资源被限流或者降级了，就会抛BlockException
        catch (BlockException e) {
            log.warn("限流，或者降级了",e);
            return "限流，或者降级了";
        }catch (IllegalArgumentException e){
            // 统计IllegalArgumentException发生的次数、占比...
            Tracer.trace(e);
            return "参数非法!";
        }
        finally {
            if (entry != null){
                // 退出entry
                entry.exit();
            }
            ContextUtil.exit();
        }
    }

    @GetMapping("/test-sentinel-resource")
    @SentinelResource(value = "test-sentinel-resource",
            blockHandler = "block",
            fallback = "fallback")
    public String testSentinelResource(@RequestParam(required = false) String a){

        // 被保护的业务逻辑
        if (StringUtils.isBlank(a)){
            throw new IllegalArgumentException("a cannot be blank");
        }
        return a;
    }

    /**
     * 处理限流或降级
     * @param a
     * @param e
     * @return
     */
    public String block( String a,BlockException e){
        log.warn("限流，或者降级了 block",e);
        return "限流，或者降级了 block";
    }

    /**
     * 处理降级
     * @param a
     * @param e
     * @return
     */
    public String fallback( String a,BlockException e){
        log.warn("限流，或者降级了 fallback",e);
        return "限流，或者降级了 fallback";
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test-rest-template-sentinel/{userId}")
    public UserDTO test(@PathVariable Integer userId) {
        return this.restTemplate
                .getForObject(
                        "http://user-center/users/{userId}",
                        UserDTO.class, userId);
    }

    @GetMapping("/tokenRelay/{userId}")
    public ResponseEntity<UserDTO> tokenRelay(@PathVariable Integer userId, HttpServletRequest request) {
        String token = request.getHeader("X-Token");
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Token", token);

        return this.restTemplate
                .exchange(
                        "http://user-center/users/{userId}",
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        UserDTO.class,
                        userId
                );
    }
}
