package tech.punklu.usercenter.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.punklu.usercenter.dao.user.UserMapper;
import tech.punklu.usercenter.domain.entity.user.User;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户id查询用户
     * @param id
     * @return
     */
    public User findById(Integer id){
        log.info("被调用了...");
        return this.userMapper.selectByPrimaryKey(id);
    }
}
