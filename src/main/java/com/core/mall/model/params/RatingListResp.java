package com.core.mall.model.params;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RatingListResp {

    private List<Rating> ratingList;

    @Data
    public static class Rating {
        private String name;
        private String userImg;
        private long ratingTime;
        private long deliveryTime;
        private BigDecimal score;
        private int rateType;
        private String ratingDesc;
        private List<String> recommend;
    }
}
