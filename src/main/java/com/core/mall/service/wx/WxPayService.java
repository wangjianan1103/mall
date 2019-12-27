package com.core.mall.service.wx;

import com.core.mall.model.params.WxSdkConfigParam;
import com.core.mall.model.params.WxSdkConfigResp;

public interface WxPayService {

    public WxSdkConfigResp wxSdkConfig(WxSdkConfigParam param);

    /**
     * 支付回调结果
     */
    public String payBack(String notifyData);
}
