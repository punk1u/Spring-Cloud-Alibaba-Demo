package tech.punklu.usercenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.punklu.usercenter.dao.user.UserMapper;
import tech.punklu.usercenter.domain.entity.user.User;

import java.util.Date;

@RestController
public class TestController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/test")
    public User testInsert(){
        User user = new User();
        user.setAvatarUrl("xxx");
        user.setWxId("111");
        user.setWxNickname("111");
        user.setRoles("111");
        user.setBonus(100);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        this.userMapper.insert(user);
        return user;
    }

    @GetMapping("/testFeignParam")
    public User testFeignParam(User user){
        return user;
    }
}
