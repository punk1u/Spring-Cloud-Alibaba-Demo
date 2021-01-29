package tech.punklu.contentcenter.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import ribbonconfiguration.RibbonConfiguration;

/**
 * 如果需要设置全局Ribbon负载均衡规则，只需要:
 * 1、将@RibbonClient注解替换成@RibbonClients注解
 * 2、将注解中的configuration属性设置为自定义的负载均衡规则类RibbonConfiguration即可
 */

@Configuration
@RibbonClient(name = "user-center",configuration = RibbonConfiguration.class)
public class UserCenterRibbonConfiguration {
}
