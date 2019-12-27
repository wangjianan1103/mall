package com.core.mall.service.wx;


import com.core.mall.model.wx.CallbackSignParam;

public interface CallbackService {

    String getCheckSignRs(String token, CallbackSignParam req);

    String handleCallback(String postData);
}
