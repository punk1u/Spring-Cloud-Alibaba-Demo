package tech.punklu.contentcenter.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * 配置Feign的日志级别
 * 不能加@Configuration注解，否则会被当成Feign的全局日志级别配置，
 * 如果加上@Configuration注解又不想被当成全局配置，需要将此类移到Spring Boot启动类所在的目录
 * 之外的地方，避免被Spring Boot扫描到导致父子上下文问题
 */
public class UserCenterFeignConfiguration {

    /**
     *

     | 级别       | 打印内容                                      |
     | ---------- | --------------------------------------------- |
     | NONE(默认) | 不记录任何日志                                |
     | BASIC      | 仅记录请求方法、URL、响应状态代码以及执行时间 |
     | HEADERS    | 记录BASIC级别的基础上，记录请求和响应的header |
     | FULL       | 记录请求和响应的header、body和元数据          |
     * @return
     */
    @Bean
    public Logger.Level level(){
        // 让Feign打印所有请求的细节
        return Logger.Level.FULL;
    }
}
