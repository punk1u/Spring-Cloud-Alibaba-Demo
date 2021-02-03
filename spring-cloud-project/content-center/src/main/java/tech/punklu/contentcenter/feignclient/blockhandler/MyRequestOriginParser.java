package tech.punklu.contentcenter.feignclient.blockhandler;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class MyRequestOriginParser implements RequestOriginParser {



    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        // 从请求参数中获取名为origin的参数并返回
        // 如果获取不到origin参数，直接抛出异常
        // 实际使用时，来源信息最好放在header中
        String origin = httpServletRequest.getParameter("origin");
        if (StringUtils.isBlank(origin)){
            throw new IllegalArgumentException("origin must be specified");
        }
        return origin;
    }
}
