package com.core.mall.model.params;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDoInfoParam {
    private String userGid;
    private BigDecimal amount;
    private String addressGid;
    private List<OrderProduct> orderProductList;
    private String orderDesc;

    @Data
    public static class OrderProduct {
        private String productGid;
        private List<OrderSpec> orderSpecList;
        private int count;
    }

    @Data
    public static class OrderSpec {
        private int specId;
        private int count;
    }
}
