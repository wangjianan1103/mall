package com.core.mall.model.params;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSpecResp {
    private long id;
    private String name;
    private BigDecimal price;
}
