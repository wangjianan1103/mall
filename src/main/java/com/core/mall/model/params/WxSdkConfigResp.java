package com.core.mall.model.params;

import lombok.Data;

@Data
public class WxSdkConfigResp {
    private String nonceStr;
    private String signature;
    private String timestamp;
    private String appId;

}
