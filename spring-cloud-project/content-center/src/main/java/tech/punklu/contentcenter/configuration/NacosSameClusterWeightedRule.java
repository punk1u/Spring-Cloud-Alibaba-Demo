package tech.punklu.contentcenter.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {


    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        // 从配置文件获取当前服务(内容中心)所在的机房(集群)名称
        String clusterName = nacosDiscoveryProperties.getClusterName();

        BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
        // 获取想要请求的微服务的名称
        String name = loadBalancer.getName();
        // 拿到服务发现的相关API
        NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();


        try {
            // 找到指定服务的所有实例 A，true表示只查找健康的可用的实例
            List<Instance> instances = namingService.selectInstances(name, true);
            // 过滤出相同集群下的所有实例 B
            List<Instance> sameClusterInstances =
                    instances.stream().filter(instance -> Objects.equals(instance.getClusterName(), clusterName)).collect(Collectors.toList());
            // 如果B是空，就用A
            List<Instance> instancesToBeChosen = new ArrayList<>();
            if (CollectionUtils.isEmpty(sameClusterInstances)){
                instancesToBeChosen = instances;
                log.warn("发生跨集群的调用,name= {}, clusterName = {}, instances = {}",
                        name,clusterName,instances);
            }else {
                instancesToBeChosen = sameClusterInstances;
            }
            // 基于权重的负载均衡算法，返回一个实例，需要使用Nacos提供的接口二次封装调用，达到从指定的实例中根据权重选择一个可用的实例的效果
            Instance instance = ExtendBalancer.getHostByRandomWeight2(instancesToBeChosen);
            log.info("选择的实例是 port = {},instance = {}",instance.getPort(),instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("发生异常了",e);
            return null;
        }
    }


}
class ExtendBalancer extends Balancer{
    public static Instance getHostByRandomWeight2(List<Instance> hosts){
        return getHostByRandomWeight(hosts);
    }
}
