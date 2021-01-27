package tech.punklu.usercenter.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.punklu.usercenter.dao.user.UserMapper;
import tech.punklu.usercenter.domain.entity.user.User;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户id查询用户
     * @param id
     * @return
     */
    public User findById(Integer id){
        return this.userMapper.selectByPrimaryKey(id);
    }
}
