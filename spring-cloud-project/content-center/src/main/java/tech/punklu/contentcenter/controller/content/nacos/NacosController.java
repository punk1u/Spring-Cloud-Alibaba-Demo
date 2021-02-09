package tech.punklu.contentcenter.controller.content.nacos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 不光需要在配置文件中自动刷新配置：refresh-enabled: true，
 * 还需要在使用到Nacos配置信息的类上添加@RefreshScope注解才能实现自动更新
 */
@RestController
@RequestMapping("/nacos")
@RefreshScope
public class NacosController {

//    @Value("${nacosKey}")
//    private String nacosKey;
//
//    @GetMapping(value = "/getNacosValue",produces = MediaType.APPLICATION_JSON_VALUE)
//    public String getNacosValue(){
//        return nacosKey;
//    }

    @Value("${commonKey}")
    private String commonKey;
    @GetMapping(value = "/getCommonValue",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCommonValue(){
        return commonKey;
    }

    @Value("${springCloudCommonKey}")
    private String springCloudCommonKey;
    @GetMapping(value = "/getSpringCloudCommonValue",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSpringCloudCommonValue(){
        return springCloudCommonKey;
    }

    @Value("${contentKey}")
    private String contentKey;
    @GetMapping(value = "/getContentValue",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getContentValue(){
        return contentKey;
    }


}
