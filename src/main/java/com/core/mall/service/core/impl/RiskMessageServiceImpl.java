package com.core.mall.service.core.impl;

import com.core.mall.model.entity.ConfigGlobal;
import com.core.mall.model.entity.RecordOrder;
import com.core.mall.model.entity.TransOrder;
import com.core.mall.model.entity.UserAddress;
import com.core.mall.model.wx.PushMessage;
import com.core.mall.repository.ConfigGlobalRepository;
import com.core.mall.repository.TransOrderRepository;
import com.core.mall.repository.UserAddressRepository;
import com.core.mall.service.core.RiskMessageService;
import com.core.mall.service.wx.WeChatPushMessageService;
import com.core.mall.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class RiskMessageServiceImpl implements RiskMessageService {
    private final static Logger logger = LoggerFactory.getLogger(RiskMessageServiceImpl.class);

    private final static String SHOP_USER_NEW_ORDER_OPEN_IDS = "shop_user_new_order_open_ids";

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private WeChatPushMessageService weChatPushMessageService;

    @Autowired
    private ConfigGlobalRepository configGlobalRepository;

    @Autowired
    private TransOrderRepository transOrderRepository;

    @Override
    public void sendNewOrderWeChatMessage(String userGid, RecordOrder recordOrder) {
        logger.info("sendNewOrderWeChatMessage, userGid={}, recordOrder={}", userGid, recordOrder);

        if (userGid == null || recordOrder == null) {
            return;
        }

        ConfigGlobal configGlobal = configGlobalRepository.findByGlobalKey(SHOP_USER_NEW_ORDER_OPEN_IDS);
        if (configGlobal == null) {
            return;
        }

        UserAddress userAddress = userAddressRepository.findByGid(recordOrder.getAddressGid());
        if (userAddress == null) {
            return;
        }

        PushMessage msg = new PushMessage();
        msg.setMsgId("shop_new_order");
        // args
        Map<String, String> argMap = new HashMap<String, String>();
        argMap.put("first", "您好，您有一笔新订单，请尽快处理。");
        argMap.put("keyword1", Utility.forMartTime(Long.parseLong(String.valueOf(recordOrder.getCreateTime())) * 1000, "yyyy-MM-dd HH:mm:ss"));
        argMap.put("keyword2", userAddress.getAddress());
        argMap.put("keyword3", recordOrder.getOrderAmount().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "元");
        argMap.put("keyword4", recordOrder.getOrderDesc());
        argMap.put("remark", "请尽快准备，尽快送达。");
        msg.setMsgTextArgs(argMap);

        String[] openIds = configGlobal.getGlobalValue().split(",");
        for (String openId : openIds) {
            msg.setOpenId(openId);
            weChatPushMessageService.sendWeChatMessage(msg);
        }
    }

    @Override
    public void sendOrderSuccess(String userGid, RecordOrder recordOrder) {
        logger.info("sendNewOrderWeChatMessage, userGid={}, recordOrder={}", userGid, recordOrder);

        if (userGid == null || recordOrder == null) {
            return;
        }

        PushMessage msg = new PushMessage();
        msg.setMsgId("shop_order_success");
        msg.setUserGid(userGid);
        // params
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("gid", recordOrder.getGid());
        msg.setMsgUrlParams(urlParams);

        // args
        Map<String, String> argMap = new HashMap<String, String>();
        argMap.put("first", "您在磐石恋家披萨微信商城的订单已购买成功！");
        argMap.put("keyword1", recordOrder.getOrderAmount().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "元");
        argMap.put("keyword2", "微信支付");
        argMap.put("remark", "我们会尽快给您派送，如有疑问，请联系15567427733");
        msg.setMsgTextArgs(argMap);

        weChatPushMessageService.sendWeChatMessage(msg);
    }

    @Override
    public void sendOrderStatusSuccess(String userGid, RecordOrder recordOrder) {
        logger.info("sendOrderStatusSuccess, userGid={}", userGid);

        if (userGid == null || recordOrder == null) {
            return;
        }

        TransOrder transOrder = transOrderRepository.findByGid(recordOrder.getTransGid());
        if (transOrder == null) {
            return;
        }

        PushMessage msg = new PushMessage();
        msg.setMsgId("shop_order_status_change");
        msg.setUserGid(userGid);

        // params
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("gid", recordOrder.getGid());
        msg.setMsgUrlParams(urlParams);

        // args
        Map<String, String> argMap = new HashMap<String, String>();
        argMap.put("first", "您好，您有一个订单状态已更新!");
        argMap.put("keyword1", transOrder.getGlobalOrderId());
        argMap.put("keyword2", "商家已接单");
        argMap.put("keyword3", Utility.forMartTime(Long.parseLong(String.valueOf(recordOrder.getCreateTime())) * 1000, "yyyy-MM-dd HH:mm:ss"));
        argMap.put("remark", "请等待商家配送。");
        msg.setMsgTextArgs(argMap);

        weChatPushMessageService.sendWeChatMessage(msg);

    }
}
