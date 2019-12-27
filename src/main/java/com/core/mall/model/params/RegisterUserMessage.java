package com.core.mall.model.params;

import com.core.mall.model.wx.WxUserInfoResponse;
import lombok.Data;

@Data
public class RegisterUserMessage {
    private int subscribe;
    private String openId;
    private String nickName;
    private int sex;
    private String city;
    private String province;
    private String country;
    private String headImgUrl;
    private String subscribeScene;
    private String unionId;

    public RegisterUserMessage() {
    }

    public RegisterUserMessage(WxUserInfoResponse response) {
        this.subscribe = response.getSubscribe();
        this.openId = response.getOpenid();
        this.nickName = response.getNickname();
        this.sex = response.getSex();
        this.city = response.getCity();
        this.province = response.getProvince();
        this.country = response.getCountry();
        this.headImgUrl = response.getHeadimgurl();
        this.subscribeScene = response.getSubscribe_scene();
        this.unionId = response.getUnionid();
    }

}
