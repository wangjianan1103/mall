package com.core.mall.model.params.user;

import lombok.Data;

@Data
public class BaseLogin {
    private long uid;
    private String t;
    private String userGid;
}
