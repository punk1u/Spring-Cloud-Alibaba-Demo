package tech.punklu.usercenter.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.punklu.usercenter.auth.CheckLogin;
import tech.punklu.usercenter.domain.entity.user.User;
import tech.punklu.usercenter.service.user.UserService;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;




    /**
     * 根据用户id查询对应的用户信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @CheckLogin
    public User findById(@PathVariable Integer id){
        return this.userService.findById(id);
    }

    /**
     * 用户登录
     * @param loginUser
     * @return
     */
    @PostMapping("/login")
    public String login(@RequestBody  User loginUser){
        String userToken = this.userService.login(loginUser);
        return userToken;
    }
}
