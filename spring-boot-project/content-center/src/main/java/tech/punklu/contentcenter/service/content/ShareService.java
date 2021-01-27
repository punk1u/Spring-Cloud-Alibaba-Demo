package tech.punklu.contentcenter.service.content;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.punklu.contentcenter.dao.content.ShareMapper;
import tech.punklu.contentcenter.domain.dto.content.ShareDTO;
import tech.punklu.contentcenter.domain.dto.user.UserDTO;
import tech.punklu.contentcenter.domain.entity.content.Share;

@Service
public class ShareService {


    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 根据内容id获取分享内容的详情
     * @param id
     * @return
     */
    public ShareDTO findById(Integer id){
        // 获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        // 获取发布人id
        Integer userId = share.getUserId();
        // 调用用户微服务查询对应的用户信息
        UserDTO userDTO = restTemplate.getForObject("http://localhost:8081/users/{id}",UserDTO.class,userId);
        // 消息装备
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share,shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }
}
