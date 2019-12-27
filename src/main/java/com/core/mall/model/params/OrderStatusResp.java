package com.core.mall.model.params;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderStatusResp {
    private String recordOrderGid;
    private BigDecimal orderAmount;
    private int status;
}
