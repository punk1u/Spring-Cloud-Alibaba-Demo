package tech.punklu.contentcenter.feignclient.blockhandler;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class MyUrlBlockHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        ErrorMsg errorMsg = null;
        if (e instanceof FlowException){
            // 限流异常
            errorMsg = ErrorMsg.builder().
                    status(100)
                    .msg("限流了")
                    .build();
        }else if (e instanceof DegradeException){
            // 降级异常
            errorMsg = ErrorMsg.builder().
                    status(101)
                    .msg("降级了")
                    .build();
        }else if (e instanceof SystemBlockException ){
            // 系统规则异常
            errorMsg = ErrorMsg.builder().
                    status(102)
                    .msg("系统规则（负载/...不满足要求）了")
                    .build();
        }else if (e instanceof AuthorityException){
            // 授权异常
            errorMsg = ErrorMsg.builder().
                    status(103)
                    .msg("授权规则不通过")
                    .build();
        }else if (e instanceof ParamFlowException){
            // 参数热点异常
            errorMsg = ErrorMsg.builder().
                    status(104)
                    .msg("热点参数限流")
                    .build();
        }
        // 设置HTTP状态码
        httpServletResponse.setStatus(500);
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setHeader("Content-Type","application/json;charset=utf-8");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        new ObjectMapper().writeValue(httpServletResponse.getWriter(),errorMsg);
    }
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ErrorMsg{
    private Integer status;

    private String msg;
}
