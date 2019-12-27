package com.core.mall.service.core;


import com.core.mall.model.entity.RecordOrder;

public interface RiskMessageService {

    /**
     * 发送新订单消息给管理员
     */
    public void sendNewOrderWeChatMessage(String userGid, RecordOrder recordOrder);

    /**
     * 用户下单成功消息
     */
    public void sendOrderSuccess(String userGid, RecordOrder recordOrder);

    /**
     * 商家接单推送用户消息
     */
    public void sendOrderStatusSuccess(String userGid, RecordOrder recordOrder);
}
