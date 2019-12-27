package com.core.mall.model.params.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginResp extends BaseLogin {

    public LoginResp() {
    }

    public LoginResp(BaseLogin baseLogin) {
        this.setUid(baseLogin.getUid());
        this.setT(baseLogin.getT());
        this.setUserGid(baseLogin.getUserGid());
    }

}
