package com.core.mall.repository;

import com.core.mall.model.entity.UserWeChatInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWeChatInfoRepository extends JpaRepository<UserWeChatInfo, Long> {

    UserWeChatInfo findByUserGid(String userGid);

    UserWeChatInfo findByOpenId(String openId);
}