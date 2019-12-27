package com.core.mall.model.params;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductListResp {

    private List<ProductTypeResponse> response;
    private BigDecimal deliveryPrice;
    private BigDecimal sumDeliveryPrice;
    private BigDecimal minPrice;

    @Data
    public static class ProductTypeResponse {
        private String name;
        private int type;
        private List<ProductResponse> foods;
    }
}
