package com.core.mall.model.params;

import lombok.Data;

import java.util.List;

@Data
public class IndexMainResp {
    private String name;
    private String image;
    private String description;
    private String bulletin;
    private List<String> pic;
    private List<String> info;
    private List<Support> supports;
    private String halt;

    @Data
    public static class Support {
        private String name;
        private int type;
    }
}
