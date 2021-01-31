package tech.punklu.contentcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tech.punklu.contentcenter.configuration.UserCenterFeignConfiguration;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("tech.punklu.contentcenter.dao")
@SpringBootApplication
// 想通过代码配置Feign全局配置，通过defaultConfiguration属性即可
@EnableFeignClients//(defaultConfiguration = UserCenterFeignConfiguration.class)
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }


    /**
     * 在Spring容器中，创建一个对象，类型是RestTemplate，
     * 名称/id是方法名
     * @return
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
