package ribbonconfiguration;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.punklu.contentcenter.configuration.NacosWeightedRule;


/**
 * Ribbon自定义规则不能在Spring Boot启动类所在的包下面，
 * 而要在和Spring Boot启动类所在的包平级的包下面创建。层级关系如下：
 * tech.punklu.contentcenter.configuration
 * 	    ContentCenterApplication (Spring Boot启动类)
 * ribbonconfiguration
 * 	    RibbonConfiguration (自定义Ribbon负载均衡规则类)
 *
 * 原因在于Spring的父子上下文问题，如果将自定义的Ribbon负载均衡规则放到了和Spring Boot启动类同层级的目录下，
 * 就会被Spring Boot扫描到自定义类上的`@Configuration`注解，
 * 从而导致最后一个被扫描到的自定义Ribbon规则类被当成全局自定义负载均衡规则。
 * 因此，为了实现细粒度的负载均衡规则，
 * 必须确保自定义的Ribbon负载均衡规则类处于独立的子上下文中而不是Spring默认的父上下文中。
 */

@Configuration
public class RibbonConfiguration {

    /**
     * 自定义内容中心调用用户中心时的Ribbon负载均衡规则为随机
     * @return
     */
    @Bean
    public IRule ribbonRule(){
        return new NacosWeightedRule();
    }

    /**
     * 自定义Ping的方式
     */
    /*@Bean
    public IPing ping(){
        return new PingUrl();
    }*/
}
