package com.core.mall.model.wx.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TextReplyMsg extends ReplyBaseMsg {
    private String Content;

}
