package com.core.mall.model.wx;

import lombok.Data;

@Data
public class TemplateMessageResponse {
    private int errcode;
    private String errmsg;
    private String msgid;
}
