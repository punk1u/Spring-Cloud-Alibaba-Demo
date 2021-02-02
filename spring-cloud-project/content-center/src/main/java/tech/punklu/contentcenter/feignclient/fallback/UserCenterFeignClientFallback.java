package tech.punklu.contentcenter.feignclient.fallback;

import org.springframework.stereotype.Component;
import tech.punklu.contentcenter.domain.dto.user.UserDTO;
import tech.punklu.contentcenter.feignclient.UserCenterFeignClient;

@Component
public class UserCenterFeignClientFallback implements UserCenterFeignClient {

    @Override
    public UserDTO findById(Integer id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setWxNickname("一个默认用户");
        return userDTO;
    }

    @Override
    public UserDTO testFeignParam(UserDTO userDTO) {
        return null;
    }
}
