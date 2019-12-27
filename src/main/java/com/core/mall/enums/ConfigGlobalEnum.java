package com.core.mall.enums;

/**
 * global 全局key
 */
public enum ConfigGlobalEnum {
    CONFIG_GLOBAL_KEY_DELIVERY_PRICE("config_global_key_delivery_price_key", "配送费"),
    CONFIG_GLOBAL_KEY_SUM_DELIVERY_PRICE("config_global_sum_key_delivery_price_key", ""),
    CONFIG_GLOBAL_KEY_MIN_PRICE("config_global_key_min_price_key", "最低购买金额"),
    CONFIG_GLOBAL_KEY_ORDER_STATUS_SUCCESS("config.order_status_success", "定时处理状态由 已送达(已完成)"),
    CONFIG_INDEX_SHOP_NAME("config.index.shop_name", "商店名称"),
    CONFIG_INDEX_SHOP_IMG_URL("config.index.shop_img_url", "商店图片地址"),
    CONFIG_INDEX_SHOP_DESC("config.index.shop_desc", "活动信息"),
    CONFIG_INDEX_SHOP_INFO("config.index.shop_info", "商店信息"),
    CONFIG_INDEX_SHOP_PIC("config.index.shop_pic", "后厨图片"),
    CONFIG_INDEX_SHOP_SUPPORT("config.index.shop_support", "活动信息"),
    CONFIG_INDEX_SHOP_DESCRIPTION("config.index.shop_description", "配送描述"),
    CONFIG_INDEX_BUSINESS_START_TIME("config.index.business_start_time", "营业开始时间"),
    CONFIG_INDEX_BUSINESS_END_TIME("config.index.business_end_time", "营业关闭时间"),
    CONFIG_DEFAULT_SELLER_ORDER_TIME("config.default.seller_order_time", "默认接单时间"),
    CONFIG_INDEX_BUSINESS_SHOP_HALT("config.index.business_shop_halt", "是否营业");

    private final String value;

    private final String desc;

    private ConfigGlobalEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return this.value;
    }

    public String getDesc() {
        return desc;
    }
}
