package tech.punklu.usercenter.auth;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.punklu.usercenter.util.JWTOperater;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class CheckLoginAspect {

    @Autowired
    private JWTOperater jwtOperater;

    /**
     * 检查用户是否登录
     * @param point
     * @return
     */
    @Around("@annotation(tech.punklu.usercenter.auth.CheckLogin)")
    public Object checjLogin(ProceedingJoinPoint point) {
        try {
            // 从Header中获取token
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("X-Token");
            // 校验token是否合法，如果不合法直接抛异常，如果合法放行
            String id = jwtOperater.getInfoFromJWT(token, "user_id");
            if (StringUtils.isEmpty(id)){
                throw new SecurityException("Token不合法!");
            }
            // 如果校验成功，那么就将用户的id设置到request的attribute里面
            request.setAttribute("id",id);
            return  point.proceed();
        }catch (Throwable e){
            throw new SecurityException("Token不合法!");
        }
    }
}
