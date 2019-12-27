package com.core.mall.model.params;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDetailResp {
    private Address address;
    private List<Food> foodList;
    private OrderInfo orderInfo;

    @Data
    public static class OrderInfo {
        private String recordOrderGid;
        private String globalOrderId;
        private BigDecimal orderAmount;
        private BigDecimal fareAmount;
        private BigDecimal shopAmount;
        private String desc;
        private long createTime;
        private long endTime;
        private int status;
    }

    @Data
    public static class Food {
        private String gid;
        private String icon;
        private String name;
        private String description;
        private BigDecimal price;
        private int count;
        private int specType;
        private String specName;
    }

    @Data
    public static class Address {
        private String gid;
        private String name;
        private String mobile;
        private String address;
    }
}
