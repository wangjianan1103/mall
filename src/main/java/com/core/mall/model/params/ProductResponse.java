package com.core.mall.model.params;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
    private String gid;
    private String name;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private String description;
    private String info;
    private int sellCount;
    private int rating;
    private String icon;
    private String image;
    private int specType;
    private List<ProductSpecResp> specList;
}
