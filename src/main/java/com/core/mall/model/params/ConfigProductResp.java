package com.core.mall.model.params;

import lombok.Data;

import java.util.List;

@Data
public class ConfigProductResp {

    private List<ProductType> list;

    @Data
    public static class ProductType {
        private String name;
        private String ico;
        private int typeId;
        private boolean isValid;
        private double sequence;
    }
}
