package com.core.mall.service.wx.impl;

import com.core.mall.config.CoreException;
import com.core.mall.enums.ErrorCodeEnum;
import com.core.mall.model.params.WxSdkConfigParam;
import com.core.mall.model.params.WxSdkConfigResp;
import com.core.mall.model.wx.JsApiTicketResponse;
import com.core.mall.service.core.TransactionService;
import com.core.mall.service.wx.WxPayService;
import com.core.mall.service.http.HttpService;
import com.core.mall.service.wx.WxTokenService;
import com.core.mall.util.Utility;
import com.core.mall.enums.WxWebUrlEnum;
import com.core.wxpay.config.IWxPayConfig;
import com.core.wxpay.config.JSSign;
import com.core.wxpay.sdk.WXPay;
import com.core.wxpay.sdk.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WxPayServiceImpl extends AbstractWxService implements WxPayService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static ConcurrentHashMap<String, String> TICKET_MAP = new ConcurrentHashMap<>();
    private static final String JSAPI_TICKET = "jsapi_ticket";
    private static final String EXPIRES_IN = "expires_in";

    @Autowired
    private IWxPayConfig iWxPayConfig;
    @Autowired
    private WxTokenService wxTokenService;
    @Autowired
    private HttpService httpService;
    @Autowired
    private TransactionService transactionService;

    @Override
    public WxSdkConfigResp wxSdkConfig(WxSdkConfigParam param) {
        logger.info("wxSdkConfig, start, param={}", param);
        if (param == null) {
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR.getErrorCode());
        }
        WxSdkConfigResp resp = new WxSdkConfigResp();
        Map<String, String> result = JSSign.sign(this.getJSAPITicket(), param.getUrl());

        resp.setAppId(iWxPayConfig.getAppID());
        resp.setNonceStr(result.get("nonceStr"));
        resp.setSignature(result.get("signature"));
        resp.setTimestamp(result.get("timestamp"));
        logger.info("wxSdkConfig, end, resp={}", resp);
        return resp;
    }

    private String getJSAPITicket() {
        String JsApiTicket = null;

        if (TICKET_MAP.get(JSAPI_TICKET) != null && Integer.parseInt(TICKET_MAP.get(EXPIRES_IN)) > Utility.getCurrentTimeStamp()) {
            JsApiTicket = TICKET_MAP.get(JSAPI_TICKET);
            logger.info("getJSAPITicket() end, js_api_ticket from redis, expires_in, accessToken={}", JsApiTicket);
            return JsApiTicket;
        }

        String accessToken = wxTokenService.getWxToken();

        // params
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(WxWebUrlEnum.UrlParam.ACCESS_TOKEN, accessToken);
        JsApiTicketResponse response = null;
        try {
            response = httpService.get(this.fmtReqWebsUrl(WxWebUrlEnum.GET_TICKET, paramMap), JsApiTicketResponse.class);
        } catch (Exception e) {
            logger.error("getJSAPITicket() exception. ", e);
        }

        if (response != null) {
            JsApiTicket = response.getTicket();
            Integer expiresIn = response.getExpires_in();
            TICKET_MAP.put(JSAPI_TICKET, JsApiTicket);
            TICKET_MAP.put(EXPIRES_IN, String.valueOf(expiresIn + Utility.getCurrentTimeStamp() - 600));
        }
        logger.info("getJSAPITicket() end, JsApiTicket={}", JsApiTicket);
        return JsApiTicket;
    }

    @Override
    public String payBack(String notifyData) {
        logger.info("payBack() start, notifyData={}", notifyData);
        String xmlBack="";
        Map<String, String> notifyMap = null;
        try {
            WXPay wxpay = new WXPay(iWxPayConfig);

            notifyMap = WXPayUtil.xmlToMap(notifyData);         // 转换成map
            if (wxpay.isPayResultNotifySignatureValid(notifyMap)) {
                // 签名正确
                // 进行处理。
                // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
                String return_code = notifyMap.get("return_code");//状态
                String out_trade_no = notifyMap.get("out_trade_no");//订单号

                if (out_trade_no == null) {
                    logger.info("微信支付回调失败订单号: {}", notifyMap);
                    xmlBack = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                    return xmlBack;
                }

                transactionService.payCallback(out_trade_no, return_code);
                logger.info("微信支付回调成功订单号: {}", notifyMap);
                xmlBack = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[SUCCESS]]></return_msg>" + "</xml> ";
                return xmlBack;
            } else {
                logger.error("微信支付回调通知签名错误");
                xmlBack = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                return xmlBack;
            }
        } catch (Exception e) {
            logger.error("微信支付回调通知失败",e);
            xmlBack = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
        return xmlBack;
    }
}
