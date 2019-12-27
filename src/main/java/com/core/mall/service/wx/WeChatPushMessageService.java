package com.core.mall.service.wx;

import com.core.mall.model.wx.PushMessage;

public interface WeChatPushMessageService {

    void sendWeChatMessage(PushMessage pushMessage);
}
