package com.core.mall.model.wx;

import lombok.Data;

@Data
public class CallbackSignParam {
    private String signature;
    private String timestamp;
    private String nonce;
    private String echoStr;

    public CallbackSignParam() {
    }

    public CallbackSignParam(String signature, String timestamp, String nonce) {
        this.signature = signature;
        this.timestamp = timestamp;
        this.nonce = nonce;
    }

    public CallbackSignParam(String signature, String timestamp, String nonce, String echoStr) {
        this(signature, timestamp, nonce);
        this.echoStr = echoStr;
    }

}
