package com.core.mall.model.wx;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class PushMessage implements Serializable {

    private String msgId;

    private String userGid;

    private String openId;

    private Map<String, String> msgTextArgs;

    private Map<String, String> msgUrlParams;

}
