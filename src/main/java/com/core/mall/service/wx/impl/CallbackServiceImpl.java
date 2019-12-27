package com.core.mall.service.wx.impl;

import com.core.mall.model.wx.CallbackSignParam;
import com.core.mall.model.wx.message.TextMessage;
import com.core.mall.model.wx.message.TextReplyMsg;
import com.core.mall.service.core.UserBaseService;
import com.core.mall.service.wx.CallbackService;
import com.core.mall.service.wx.WxUserInfoService;
import com.core.mall.util.MessageUtil;
import com.core.mall.util.Utility;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class CallbackServiceImpl implements CallbackService {
    private final static Logger logger = LoggerFactory.getLogger(CallbackServiceImpl.class);
    private final static String SUCCESS_RSP = "success";

    private final Set<String> eventSet = new HashSet<>();

    @Autowired
    private WxUserInfoService wxUserInfoService;
    @Autowired
    private UserBaseService userBaseService;

    @PostConstruct
    private void init() {
        for (DealEvent e : DealEvent.values()) {
            eventSet.add(e.name());
        }
        logger.debug("CallbackServiceImpl-init: eventSet.size={}", eventSet.size());
    }

    @Override
    public String getCheckSignRs(String token, CallbackSignParam req) {
        final long bt = System.currentTimeMillis();
        logger.debug("getCheckSignRs: arg token={}", token);
        try {

            // do check
            final String signature = req.getSignature();
            final String echoStr = req.getEchoStr();
            if (echoStr == null || echoStr.isEmpty() || signature == null || signature.isEmpty()) {
                logger.warn("getCheckSignRs: echoStr or signature is null.");
                return null;
            }
            // body
            String timestamp = req.getTimestamp();
            String nonce = req.getNonce();
            //
            // token timestamp nonce　to sort
            String[] tmps = new String[]{token, timestamp, nonce};
            Arrays.sort(tmps);
            StringBuilder content = new StringBuilder();
            for (String s : tmps) {
                content.append(s);
            }
            // sha1 digest
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            content = new StringBuilder(Hex.encodeHexString(md.digest(content.toString().getBytes())));
            logger.debug("getCheckSignRs: signature={},target content={}", signature, content.toString());
            if (!signature.equals(content.toString())) {
                logger.warn("getCheckSignRs: signature invalid.");
                return null;
            }
            return echoStr;
        } catch (Exception e) {
            logger.error("getCheckSignRs: catch e.", e);
        } finally {
            logger.debug("getCheckSignRs: end. use time={}ms.", (System.currentTimeMillis() - bt));
        }
        return null;
    }

    @Override
    public String handleCallback(String postData) {
        logger.debug("handleCallback: postData.len={}", (postData != null ? postData.length() : 0));
        if (postData == null) {
            logger.warn("handleCallback: clientTag or postData is null.");
            return SUCCESS_RSP;
        }
        try {
            // 调用parseXml方法解析请求消息
            Map<String, String> requestMap = MessageUtil.parseXml(postData);

            String msgType = requestMap.get(MessageUtil.MSG_TYPE_XPATH);
            String fromUserName = requestMap.get(MessageUtil.MSG_FROM_USER_NAME);
            String toUserName = requestMap.get(MessageUtil.MSG_TO_USER_NAME);
            logger.debug("handleCallback: postData requestMap is success, requestMap={}, openId={}", requestMap, fromUserName);

            if (MessageUtil.REQ_MESSAGE_TYPE_TEXT.equals(msgType)) {
                // 回复文本消息
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(Utility.getCurrentTimeStamp() * 1000);
                textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                textMessage.setContent("感谢您的关注, 订餐电话: 15567427733");
                return MessageUtil.messageToXml(textMessage);
            } else if (MessageUtil.REQ_MESSAGE_TYPE_EVENT.equals(msgType)) {
                // 事件类型
                String eventType = requestMap.get(MessageUtil.EVENT_EVENT);
                DealEvent.wxUserInfoService = this.wxUserInfoService;
                DealEvent.userBaseService = this.userBaseService;
                logger.debug("handleCallback: postData msgType is event, msgType={}, eventType={}", msgType, eventType);
                return DealEvent.valueOf(eventType.toLowerCase()).handleEvent(requestMap);
            }
        } catch (Exception e) {
            logger.error("handleCallback: catch e.", e);
        } finally {
            logger.debug("handleCallback: end.");
        }
        return SUCCESS_RSP;
    }

    public enum DealEvent {
        subscribe {
            // subscribe(订阅/注册)
            @Override
            public String handleEvent(Map<String, String> requestMap) {
                try {
                    String fromUserName = requestMap.get(MessageUtil.MSG_FROM_USER_NAME);
                    String toUserName = requestMap.get(MessageUtil.MSG_TO_USER_NAME);

                    wxUserInfoService.doSubscribe(fromUserName);

                    TextMessage textMessage = new TextMessage();
                    textMessage.setToUserName(fromUserName);
                    textMessage.setFromUserName(toUserName);
                    textMessage.setCreateTime(Utility.getCurrentTimeStamp());
                    textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    textMessage.setContent("欢迎关注恋家披萨，订餐电话: 15567427733");
                    return MessageUtil.messageToXml(textMessage);
                } catch (Exception e) {
                    logger.debug("handleEvent subscribe is error, exception", e);
                }
                return SUCCESS_RSP;
            }
        },
        unsubscribe {
            // subscribe(取消订阅/取消注册)
            @Override
            public String handleEvent(Map<String, String> requestMap) {
                // 取消关注
                String fromUserName = requestMap.get(MessageUtil.MSG_FROM_USER_NAME);
                userBaseService.unRegisterWx(fromUserName);
                return SUCCESS_RSP;
            }
        },
        scan {
            // scan(用户已关注时的扫描带参数二维码)
            @Override
            public String handleEvent(Map<String, String> requestMap) {
                return SUCCESS_RSP;
            }
        },
        location {
            // LOCATION(上报地理位置)
            @Override
            public String handleEvent(Map<String, String> requestMap) {
                return SUCCESS_RSP;
            }
        },
        click {
            // CLICK(自定义菜单)
            @Override
            public String handleEvent(Map<String, String> requestMap) {
                try {
                    String eventKey = requestMap.get(MessageUtil.EVENT_EVENT_KEY);
                    String ReplyType = "text";

                    long createTime = Utility.getCurrentTimeStamp();
                    String fromUserName = requestMap.get(MessageUtil.MSG_FROM_USER_NAME);
                    String toUserName = requestMap.get(MessageUtil.MSG_TO_USER_NAME);

                    switch (ReplyType) {
                        case "text": {
                            TextReplyMsg rsp = new TextReplyMsg();
                            rsp.setCreateTime(createTime);
                            rsp.setFromUserName(toUserName);
                            rsp.setToUserName(fromUserName);
                            rsp.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                            rsp.setContent("欢迎来到恋家披萨网上商城，联系电话: 15567427733");
                            if ("lianjia_dingcan".equals(eventKey)) {
                                rsp.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                                rsp.setContent("欢迎来到恋家披萨网上商城，订餐电话: 15567427733");
                            } else if ("lianxiwomen".equals(eventKey)) {
                                rsp.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                                rsp.setContent("欢迎来到恋家披萨网上商城，联系电话: 15567427733");
                            }
                            return MessageUtil.messageToXml(rsp);
                        }
                        case "news": {
                            return SUCCESS_RSP;
                        }
                        case "image": {
                            return SUCCESS_RSP;
                        }
                        case "voice": {
                            return SUCCESS_RSP;
                        }
                        case "video": {
                            return SUCCESS_RSP;
                        }
                        case "music": {
                            return SUCCESS_RSP;
                        }
                        default: {
                            logger.warn("click event: no accord with content");
                            return SUCCESS_RSP;
                        }
                    }
                } catch (Exception e) {
                    logger.debug("handleEvent click is error, exception", e);
                }
                return SUCCESS_RSP;
            }
        },
        templatesendjobfinish {
            @Override
            public String handleEvent(Map<String, String> requestMap) {
                try {

                } catch (Exception e) {
                    logger.debug("handleEvent view is error, exception", e);
                }
                return SUCCESS_RSP;
            }
        },
        view {
            // VIEW(菜单跳转链接)
            @Override
            public String handleEvent(Map<String, String> requestMap) {
                try {

                } catch (Exception e) {
                    logger.debug("handleEvent view is error, exception", e);
                }
                return SUCCESS_RSP;
            }
        };
        public abstract String handleEvent(Map<String, String> requestMap);

        static WxUserInfoService wxUserInfoService;
        static UserBaseService userBaseService;
    };
}