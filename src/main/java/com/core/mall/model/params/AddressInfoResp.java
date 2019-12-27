package com.core.mall.model.params;

import lombok.Data;

import java.util.List;

@Data
public class AddressInfoResp {

    private List<AddressInfo> infoList;

    @Data
    public static class AddressInfo {
        private String gid;
        private String name;
        private String mobile;
        private int sex;
        private String address;
        private int tag;

    }
}
