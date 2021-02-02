package tech.punklu.contentcenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tech.punklu.contentcenter.configuration.UserCenterFeignConfiguration;
import tech.punklu.contentcenter.domain.dto.user.UserDTO;
import tech.punklu.contentcenter.feignclient.fallback.UserCenterFeignClientFallback;
import tech.punklu.contentcenter.feignclient.fallback.UserCenterFeignClientFallbackFactory;

/**
 * 调用用户中心的Feign客户端代理类
 */

/**
 * fallback和fallbackFactory只应使用一个
 */
//@FeignClient(name = "user-center",configuration = UserCenterFeignConfiguration.class)
//@FeignClient(name = "user-center",fallback = UserCenterFeignClientFallback.class)
@FeignClient(name = "user-center",fallbackFactory = UserCenterFeignClientFallbackFactory.class)
public interface UserCenterFeignClient {

    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);


    /**
     * Feign构造多参数的GET请求的方式：
     * 1、在类参数上添加@SpringQueryMap注解
     * 2、使用@RequestParam注解
     * 3、使用Map来构建参数
     */
    @GetMapping("testFeignParam")
    UserDTO testFeignParam(@SpringQueryMap UserDTO userDTO);

    /**
     * Feign构造多参数的POST请求的方式
     * 使用Spring MVC的@RequestBody注解
     */
}
