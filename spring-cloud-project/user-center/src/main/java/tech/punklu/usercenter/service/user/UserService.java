package tech.punklu.usercenter.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.punklu.usercenter.dao.user.UserMapper;
import tech.punklu.usercenter.domain.entity.user.User;
import tech.punklu.usercenter.util.JWTOperater;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTOperater jwtOperater;

    /**
     * 根据用户id查询用户
     * @param id
     * @return
     */
    public User findById(Integer id){
        log.info("被调用了...");
        return this.userMapper.selectByPrimaryKey(id);
    }

    public String login(User user){
        User dbData = this.userMapper.selectByPrimaryKey(user);
        // 如果登录成功，生成对应的jwt token并返回
        if (dbData != null){
            Map<String,String> parameterMap = new HashMap<>();
            parameterMap.put("user_id",dbData.getId().toString());
            String token = jwtOperater.generateToken(parameterMap);
            return token;
        }else {
            log.warn("登录失败，不存在对应的用户!");
        }
        return null;
    }
}
