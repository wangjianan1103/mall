package com.core.mall.service.wx.impl;

import com.core.mall.config.CoreException;
import com.core.mall.enums.ErrorCodeEnum;
import com.core.mall.model.params.RegisterUserMessage;
import com.core.mall.model.wx.OAuthResponse;
import com.core.mall.model.wx.WxUserInfoResponse;
import com.core.mall.service.core.UserBaseService;
import com.core.mall.service.http.HttpService;
import com.core.mall.service.wx.WxUserInfoService;
import com.core.mall.util.GlobalConstants;
import com.core.mall.enums.WxWebUrlEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WxUserInfoServiceImpl extends AbstractWxService implements WxUserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(WxUserInfoServiceImpl.class);

    @Value("${vendor.wx.mp.app_id}")
    private String app_id;

    @Value("${vendor.wx.mp.app_secret}")
    private String app_secret;

    @Autowired
    private HttpService httpService;
    @Autowired
    private UserBaseService userBaseService;

    @Override
    public WxUserInfoResponse getUserInfo(String openid) {
        logger.debug("getUserInfo() start, openid={}", openid);
        if (openid == null) {
            logger.error("getUserInfo() openid is null");
            throw new CoreException(ErrorCodeEnum.SYS_FAIL);
        }

        WxUserInfoResponse response = null;
        try {
            // params
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put(WxWebUrlEnum.UrlParam.OPENID, openid);
            response = httpService.get(this.fmtReqWebsUrl(WxWebUrlEnum.GET_EX_USER_INFO, paramMap), WxUserInfoResponse.class);
            logger.info("getUserInfo() success, response={}", response);
        }  catch (Exception e) {
            logger.error("getUserInfo() is error, e ", e);
            throw new CoreException(ErrorCodeEnum.SYS_FAIL);
        }
        return response;
    }

    @Override
    public String doSubscribe(String openid) {
        logger.debug("doSubscribe() start, openid={}", openid);
        if (openid == null) {
            logger.error("doSubscribe() openid is null");
            return null;
        }

        WxUserInfoResponse userInfo = this.getUserInfo(openid);

        if (userInfo == null || userInfo.getSubscribe() == GlobalConstants.WxSubscribe.WX_SUBSCRIBE_UN_FOLLOWED) {
            logger.error("getUserInfo() user is un followed");
            throw new CoreException(ErrorCodeEnum.SYS_FAIL);
        }

        RegisterUserMessage message = new RegisterUserMessage(userInfo);
        // 用户注册
        String userGid = userBaseService.registerWX(message);
        return userGid;
    }

    @Override
    public OAuthResponse getOAuthByCode(String code) {
        logger.debug("getOAuthByCode() start, code={}", code);
        if (code == null) {
            logger.error("getOAuthByCode() code is null");
            return null;
        }

        OAuthResponse response = null;
        try {
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put(WxWebUrlEnum.UrlParam.CODE, code);
            paramMap.put(WxWebUrlEnum.UrlParam.APP_ID, app_id);
            paramMap.put(WxWebUrlEnum.UrlParam.APP_SECRET, app_secret);

            response = httpService.get(this.fmtReqWebsUrl(WxWebUrlEnum.GET_WX_O_AUTH_2, paramMap), OAuthResponse.class);
        } catch (Exception e) {
            logger.error("getOAuthByCode() is error, e ", e);
        }
        logger.info("getOAuthByCode() success, response={}", response);
        return response;
    }
}
