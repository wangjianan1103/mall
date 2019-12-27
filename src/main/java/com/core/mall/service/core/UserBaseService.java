package com.core.mall.service.core;


import com.core.mall.model.params.*;
import com.core.mall.model.params.user.LoginParam;
import com.core.mall.model.params.user.LoginResp;
import com.core.mall.model.params.user.RegisterParam;
import com.core.mall.model.params.user.RegisterResp;

public interface UserBaseService {

    /**
     * 微信
     *
     * 关注、注册
     */
    String registerWX(RegisterUserMessage message);

    /**
     * 微信
     *
     * 取消关注
     */
    void unRegisterWx(String openId);

    /**
     * 微信
     *
     * 授权登录，换取用户信息
     */
    WeChatInfoResp weChatInfo(WeChatInfoParam req);

    /**
     * 获取我的页面信息
     */
    MineInfoResp getMineInfo(String userGid);

    /**
     * 注册、登录
     */
    LoginResp login(LoginParam param);

    RegisterResp register(RegisterParam param);

}
