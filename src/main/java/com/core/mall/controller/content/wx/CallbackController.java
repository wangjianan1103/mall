package com.core.mall.controller.content.wx;

import com.core.mall.model.wx.CallbackSignParam;
import com.core.mall.service.wx.CallbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CallbackController {
    private final static Logger logger = LoggerFactory.getLogger(CallbackController.class);
    private final static String TOKEN = "lianjia";

    @Autowired
    private CallbackService callbackService;

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public String doGetCheckSignRs(@RequestParam(value = "signature", required = false) String signature,
                                   @RequestParam(value = "timestamp", required = false) String timestamp,
                                   @RequestParam(value = "nonce", required = false) String nonce,
                                   @RequestParam(value = "echostr", required = false) String echoStr) {
        CallbackSignParam req = new CallbackSignParam();
        req.setSignature(signature);
        req.setTimestamp(timestamp);
        req.setNonce(nonce);
        req.setEchoStr(echoStr);
        return callbackService.getCheckSignRs(TOKEN, req);
    }

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public String doMessageCallBack(@RequestParam(value = "signature", required = false) String signature,
                                   @RequestParam(value = "timestamp", required = false) String timestamp,
                                   @RequestParam(value = "nonce", required = false) String nonce,
                                   @RequestBody String postData) {
        String ret = "success";
        if (this.doGetCheckSignRs(signature, timestamp, nonce, ret) == null) {
            return ret;
        }
        ret = callbackService.handleCallback(postData);
        return ret;
    }

}
