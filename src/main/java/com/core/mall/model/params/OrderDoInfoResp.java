package com.core.mall.model.params;

import lombok.Data;

import java.util.Map;

@Data
public class OrderDoInfoResp {
    private String recordOrderGid;
    private String prepayId;
    private Map<String, String> resultMap;
}
