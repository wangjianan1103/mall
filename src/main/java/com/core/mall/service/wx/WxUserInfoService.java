package com.core.mall.service.wx;

import com.core.mall.model.wx.OAuthResponse;
import com.core.mall.model.wx.WxUserInfoResponse;

public interface WxUserInfoService {

    public WxUserInfoResponse getUserInfo(String openid);

    public String doSubscribe(String openid);

    public OAuthResponse getOAuthByCode(String code);
}
