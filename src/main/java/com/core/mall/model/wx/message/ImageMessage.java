package com.core.mall.model.wx.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImageMessage extends BaseMessage {
    // 图片链接
    private String PicUrl;
    private String MediaId;

}
