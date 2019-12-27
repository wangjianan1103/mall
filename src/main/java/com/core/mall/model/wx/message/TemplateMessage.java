package com.core.mall.model.wx.message;

import lombok.Data;

import java.util.Map;

@Data
public class TemplateMessage {
    private String touser;
    private String template_id;
    private String url;
    private Map<String, String> miniprogram;
    private Map<String, TemplateData> data;

    @Data
    public static class TemplateData {
        private String value;
        private String color;
    }
}
