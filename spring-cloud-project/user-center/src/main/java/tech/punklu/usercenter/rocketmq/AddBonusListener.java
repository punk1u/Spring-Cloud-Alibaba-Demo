package tech.punklu.usercenter.rocketmq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.punklu.usercenter.dao.bonus.BonusEventLogMapper;
import tech.punklu.usercenter.dao.user.UserMapper;
import tech.punklu.usercenter.domain.dto.message.UserAddBonusMsgDTO;
import tech.punklu.usercenter.domain.entity.bonus.BonusEventLog;
import tech.punklu.usercenter.domain.entity.user.User;

import java.util.Date;

@Service
@RocketMQMessageListener(consumerGroup = "consumer-group",topic = "add-bonus")
public class AddBonusListener implements RocketMQListener<UserAddBonusMsgDTO> {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BonusEventLogMapper bonusEventLogMapper;

    @Override
    public void onMessage(UserAddBonusMsgDTO userAddBonusMsgDTO) {
        // 当收到消息时，执行的业务
        // 1、为用户加积分
        Integer userId = userAddBonusMsgDTO.getUserId();
        // 要加的积分
        Integer bonus = userAddBonusMsgDTO.getBonus();
        User user = this.userMapper.selectByPrimaryKey(userId);
        user.setBonus(user.getBonus() + userAddBonusMsgDTO.getBonus());
        this.userMapper.updateByPrimaryKeySelective(user);
        // 2、记录日志到bonus_event_log表里面
        this.bonusEventLogMapper.insert(
                        BonusEventLog.builder()
                        .userId(userId)
                        .value(bonus)
                        .event("CONTRIBUTE")
                        .createTime(new Date())
                        .description("投稿加积分...")
                        .build()
        );
    }
}
