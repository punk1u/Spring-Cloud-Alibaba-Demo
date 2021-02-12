package tech.punklu.contentcenter.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JWTOperater {

    @Value("${JWT_KEY}")
    private String JWT_KEY;

    @Value("${JWT_EXPIRE_TIME}")
    private Long JWT_EXPIRE_TIME;

    /**
     * 生成对应的JWT Token
     * @param map
     * @return
     */
    public String generateToken(Map<String,String> map){
        // 账号密码正确
        // 指定JWT使用的算法和对应的密钥key
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        JWTCreator.Builder builder = JWT.create();
        for (String key : map.keySet()){
            builder.withClaim(key,map.get(key));
        }
        // 设置token过期时间
        String token =
                builder.withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRE_TIME))
                .sign(algorithm);
        return token;
    }

    /**
     * 从JWT Token中解析相应的数据
     * @param token JWT Token
     * @param key 要解析的数据的key
     * @return
     */
    public String getInfoFromJWT(String token,String key){
        // 指定JWT使用的算法和对应的密钥key
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            // 返回该token对应的用户名
            return jwt.getClaim(key).asString();
        }catch (TokenExpiredException e){
            // token过期
            log.warn("token过期!",e);
        }catch (JWTDecodeException e){
            // 解码失败，token错误
            log.warn("token解码失败!",e);
        }

        return null;
    }
}
