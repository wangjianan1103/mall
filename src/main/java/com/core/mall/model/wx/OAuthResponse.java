package com.core.mall.model.wx;

import lombok.Data;

@Data
public class OAuthResponse {
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String openid;
    private String scope;

}
