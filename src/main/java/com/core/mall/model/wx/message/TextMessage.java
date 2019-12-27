package com.core.mall.model.wx.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TextMessage extends BaseMessage {
    // 消息内容
    private String Content;
}
