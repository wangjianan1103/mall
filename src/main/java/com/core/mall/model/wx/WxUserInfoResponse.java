package com.core.mall.model.wx;

import lombok.Data;

@Data
public class WxUserInfoResponse {
    // 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
    private int subscribe;
    // 用户的标识
    private String openid;
    // 昵称
    private String nickname;
    // 用户的性别（1是男性，2是女性，0是未知）
    private int sex;
    // 用户所在城市
    private String city;
    // 用户所在省份
    private String province;
    // 用户所在国家
    private String country;
    // 用户头像
    private String headimgurl;
    // 返回用户关注的渠道来源
    private String subscribe_scene;
    // 微信开放平台帐号
    private String unionid;
}
