package com.core.mall.controller.content.wx;

import com.core.mall.model.params.WxSdkConfigParam;
import com.core.mall.model.params.WxSdkConfigResp;
import com.core.mall.service.wx.WxPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 微信支付相关接口
 */
@RestController
@RequestMapping(value = "wx")
public class WxPaymentController {
    private final static Logger logger = LoggerFactory.getLogger(WxPaymentController.class);

    @Autowired
    private WxPayService wxPayService;

    /**
     * 微信支付配置
     */
    @RequestMapping(value = "getSdkConfig", method = RequestMethod.POST)
    public WxSdkConfigResp getSdkConfig(@RequestBody(required = false) WxSdkConfigParam param) {
        return wxPayService.wxSdkConfig(param);
    }

    @RequestMapping(value = "/payCallback", method = RequestMethod.POST)
    public String payCallback(HttpServletRequest request, HttpServletResponse response) {
        logger.info("进入微信支付异步通知");
        String resXml="";
        try{
            //
            InputStream is = request.getInputStream();
            //将InputStream转换成String
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resXml=sb.toString();
            logger.info("微信支付异步通知请求包: {}", resXml);
            return wxPayService.payBack(resXml);
        }catch (Exception e){
            logger.error("微信支付回调通知失败",e);
            String result = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            return result;
        }
    }
}
