package com.core.mall.service.wx.impl;

import com.core.mall.model.entity.ConfigPushMessage;
import com.core.mall.model.entity.UserWeChatInfo;
import com.core.mall.model.wx.PushMessage;
import com.core.mall.model.wx.TemplateMessageResponse;
import com.core.mall.model.wx.message.TemplateMessage;
import com.core.mall.repository.ConfigPushMessageRepository;
import com.core.mall.repository.UserWeChatInfoRepository;
import com.core.mall.service.http.HttpService;
import com.core.mall.service.wx.WeChatPushMessageService;
import com.core.mall.service.wx.WxTokenService;
import com.core.mall.util.DistributedLocker;
import com.core.mall.util.Utility;
import com.core.mall.enums.WxWebUrlEnum;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeChatPushMessageServiceImpl extends AbstractWxService implements WeChatPushMessageService {
    private final static Logger logger = LoggerFactory.getLogger(WeChatPushMessageServiceImpl.class);

    @Autowired
    private DistributedLocker distributedLocker;

    @Autowired
    private ConfigPushMessageRepository configPushMessageRepository;

    @Autowired
    private UserWeChatInfoRepository userWeChatInfoRepository;

    @Autowired
    private HttpService httpService;

    @Autowired
    private WxTokenService wxTokenService;

    @Override
    public void sendWeChatMessage(PushMessage pushMessage) {
        logger.debug("sendWeChatMessage: pushMessage: {}", pushMessage);
        if (pushMessage == null) {
            logger.warn("sendWeChatMessage: pushMessage is null.");
            return;
        }
        final String msgId = pushMessage.getMsgId();
        if (Utility.isBlank(msgId)) {
            logger.warn("sendWeChatMessage: msgId is null.");
            return;
        }
        final ConfigPushMessage message = configPushMessageRepository.findByMessageId(msgId);
        if (message == null) {
            logger.warn("sendWeChatMessage: message is null, messageId={}", msgId);
            return;
        }
        String openId = pushMessage.getOpenId();
        final String userGid = pushMessage.getUserGid();
        if (Utility.isBlank(openId)) { // s1:
            if (Utility.isBlank(userGid)) {
                logger.warn("sendWeChatMessage: userGid is null.");
                return;
            }
            final UserWeChatInfo weChatInfo = userWeChatInfoRepository.findByUserGid(userGid);
            if (weChatInfo != null) {
                openId = weChatInfo.getOpenId();
            }
        }
        if (Utility.isBlank(openId)) { // s2:
            logger.warn("sendWeChatMessage: openId is empty(second).");
            return;
        }
        // lock
//        final String lockName = Utility.getLockerKey("send_we_chat_message_" + msgId + openId);
//        final String lockId = distributedLocker.lock(lockName, 5);
//        if (Utility.isBlank(lockId)) {
//            return;
//        }
        try {
            final String url = message.getMessageUrl();
            final String type = message.getMessageType();

            Map<String, String> urlParams = pushMessage.getMsgUrlParams();
            String tmpRedirectUrl = url;
            if (Utility.isNotBlank(url)) {
                if (urlParams != null && urlParams.size() > 0) {
                    tmpRedirectUrl = tmpRedirectUrl + "?" + (urlEncodeUtf8(urlParams));
                }
            }
            if ("template".equals(type)) {

                TemplateMessage templateMessage = new TemplateMessage();
                templateMessage.setTouser(openId);
                templateMessage.setTemplate_id(message.getPicUrl());
                templateMessage.setUrl(tmpRedirectUrl);

                Map<String, TemplateMessage.TemplateData> dataMap = new HashMap<String, TemplateMessage.TemplateData>();
                Map<String, String> textArgs = pushMessage.getMsgTextArgs();
                for (Map.Entry<String, String> entry : textArgs.entrySet()) {
                    TemplateMessage.TemplateData first = new TemplateMessage.TemplateData();
                    first.setColor("#000000");
                    first.setValue(entry.getValue());
                    dataMap.put(entry.getKey(), first);
                }
                templateMessage.setData(dataMap);

                String jsonString = new Gson().toJson(templateMessage).toString();
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put(WxWebUrlEnum.UrlParam.ACCESS_TOKEN, wxTokenService.getWxToken());
                TemplateMessageResponse response = httpService.post(this.fmtReqWebsUrl(WxWebUrlEnum.POST_SEND_MESSAGE, paramMap), jsonString, TemplateMessageResponse.class);
                logger.warn("sendWeChatMessage: response={}", response);
            }
        } catch (Exception e) {
            logger.error("e", e);
        } finally {
//            distributedLocker.unlock(lockName, lockId);
        }
    }

    private static String urlEncodeUtf8(Map<?, ?> map) {
        StringBuilder sbd = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sbd.length() > 0) {
                sbd.append("&");
            }
            sbd.append(String.format("%s=%s",
                    Utility.encodeUtf8(entry.getKey().toString()),
                    Utility.encodeUtf8(entry.getValue().toString())
            ));
        }
        return sbd.toString();
    }
}
