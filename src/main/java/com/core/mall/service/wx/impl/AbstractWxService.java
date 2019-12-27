package com.core.mall.service.wx.impl;

import com.core.mall.service.wx.WxTokenService;
import com.core.mall.util.Utility;
import com.core.mall.enums.WxWebUrlEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractWxService {

    @Autowired
    private WxTokenService wxTokenService;

    public String fmtReqWebsUrl(WxWebUrlEnum websUrl, Map<String, String> urlParamMap) {
        String url = websUrl.getUrl();
        if (urlParamMap == null) {
            urlParamMap = new HashMap<>();
        }
        // replace URL param
        if (url.contains(WxWebUrlEnum.UrlParam.ACCESS_TOKEN)) {
            // get access token
            String acToken = wxTokenService.getWxToken();
            if (Utility.isBlank(acToken)) {
                throw new IllegalStateException("access token invalid.");
            }
            urlParamMap.put(WxWebUrlEnum.UrlParam.ACCESS_TOKEN, acToken);
        }
        for (Map.Entry<String, String> entry : urlParamMap.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (Utility.isNotBlank(key) && val != null) {
                url = url.replace(key, val);
            }
        }
        return url;
    }
}
