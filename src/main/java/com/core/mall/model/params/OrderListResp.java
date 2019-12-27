package com.core.mall.model.params;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderListResp {

    private List<OrderInfo> orderInfoList;

    @Data
    public static class OrderInfo {
        private String gid;
        private String name;
        private Integer status;
        private BigDecimal orderAmount;
        private String imageUrl;
        private Integer dayOrder;
        private List<ProductOrderInfo> productOrderInfoList;
    }

    @Data
    public static class ProductOrderInfo {
        private int count;
        private BigDecimal priceAmount;
        private String productName;
        private int specType;
        private String specName;
    }
}
