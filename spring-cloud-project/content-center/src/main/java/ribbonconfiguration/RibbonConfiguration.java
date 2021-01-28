package ribbonconfiguration;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RibbonConfiguration {

    /**
     * 自定义内容中心调用用户中心时的Ribbon负载均衡规则为随机
     * @return
     */
    @Bean
    public IRule ribbonRule(){
        return new RandomRule();
    }
}
