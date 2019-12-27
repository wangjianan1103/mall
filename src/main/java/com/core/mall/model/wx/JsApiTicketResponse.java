package com.core.mall.model.wx;

import lombok.Data;

@Data
public class JsApiTicketResponse {
    private int errcode;
    private String errmsg;
    private Integer expires_in;
    private String ticket;

}
