package com.core.mall.model.params.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterResp extends BaseLogin {

    public RegisterResp() {
    }

    public RegisterResp(BaseLogin baseLogin) {
        this.setUid(baseLogin.getUid());
        this.setT(baseLogin.getT());
        this.setUserGid(baseLogin.getUserGid());
    }
}
