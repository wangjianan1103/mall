package com.core.mall.util;

/**
 * Created by wangjianan on 2018/1/25.
 */
public final class GlobalConstants {

    public static class API {
        public final static String API_STATUS_SUCCESS = "0";
        public final static String DATA_STATUS_SUCCESS = "ok";
    }

    public static class SubscribeStatus {
        public static final int SUBSCRIBE_STATUS_FOLLOWED = 1; // 已关注
        public static final int SUBSCRIBE_STATUS_UN_FOLLOWED = 2; // 取消关注
        public static final int SUBSCRIBE_STATUS_AGAIN_FOLLOWED = 3; // 再次关注
    }

    public static class LogSubscribeType {
        public static final int LOG_SUBSCRIBE_TYPE_FOLLOWED = 0; // 关注
        public static final int LOG_SUBSCRIBE_TYPE_UN_FOLLOWED = 1; // 取消关注
    }

    public static class SubscribeWay {
        public static final int SUBSCRIBE_WAY_DEFAULT = 0; // 其他
        public static final int SUBSCRIBE_WAY_ADD_SCENE_SEARCH = 1; // 公众号搜索
        public static final int SUBSCRIBE_WAY_ADD_SCENE_ACCOUNT_MIGRATION = 2; // 公众号迁移
        public static final int SUBSCRIBE_WAY_ADD_SCENE_PROFILE_CARD = 3; // 名片分享
        public static final int SUBSCRIBE_WAY_ADD_SCENE_QR_CODE = 4; // 扫描二维码
        public static final int SUBSCRIBE_WAY_ADD_SCENEPROFILE_LINK = 5; // 图文页内名称点击
        public static final int SUBSCRIBE_WAY_ADD_SCENE_PROFILE_ITEM = 6; // 图文页右上角菜单
        public static final int SUBSCRIBE_WAY_ADD_SCENE_PAID = 7; // 支付后关注
        public static final int SUBSCRIBE_WAY_ADD_SCENE_OTHERS = 8; // 其他
    }

    public static class WxSubscribe {
        public static final int WX_SUBSCRIBE_UN_FOLLOWED = 0; // 未关注公众号
    }

    public static class RecordProductSpec {
        public static final int RECORD_PRODUCT_SPEC_SIMPLE = 0; // 单规格
        public static final int RECORD_PRODUCT_SPEC_SUM = 1; // 多规格
    }

    public static class TransOrderStatus {
        public static final int TRANS_ORDER_STATUS_PAY_PROCESSING = 0; // 订单支付中
        public static final int TRANS_ORDER_STATUS_PAY_SUCCESS = 1; // 支付成功
        public static final int TRANS_ORDER_STATUS_PAY_FAIL = 2; // 支付失败
    }

    public static class TradeState {
        public static final String TRADE_STATE_SUCCESS = "SUCCESS"; // 支付成功
        public static final String TRADE_STATE_REFUND = "REFUND"; // 转入退款
        public static final String TRADE_STATE_NOTPAY = "NOTPAY"; // 未支付
        public static final String TRADE_STATE_CLOSED = "CLOSED"; // 已关闭
        public static final String TRADE_STATE_REVOKED = "REVOKED"; // 已撤销（刷卡支付）
        public static final String TRADE_STATE_USERPAYING = "USERPAYING"; // 用户支付中
        public static final String TRADE_STATE_PAYERROR = "PAYERROR"; // 支付失败(其他原因，如银行返回失败)
    }

    /**
     * 0: 订单支付中; 1: 支付成功; 2: 支付失败; 3: 商家接单; 4: 商家已发货; 5: 送货成功; 6: 取消订单; 7: 订单完成;
     */
    public static class RecordOrderStatus {
        public static final int RECORD_ORDER_STATUS_PAY_PROCESSING = 0; // 订单支付中
        public static final int RECORD_ORDER_STATUS_PAY_SUCCESS = 1; // 支付成功
        public static final int RECORD_ORDER_STATUS_PAY_FAIL = 2; // 支付失败
        public static final int RECORD_ORDER_STATUS_SELLER_PROCESSING = 3; // 商家接单
        public static final int RECORD_ORDER_STATUS_SELLER_SEND = 4; // 商家已发货
        public static final int RECORD_ORDER_STATUS_SELLER_SEND_SUCCESS = 5; // 送货成功
        public static final int RECORD_ORDER_STATUS_CANCEL = 6; // 取消订单
        public static final int RECORD_ORDER_STATUS_SUCCESS = 7; // 订单完成
    }

    /**
     * 页面显示订单状态; 0: 待支付; 1: 待接单; 2: 支付失败; 3: 正在出餐;  4: 取消订单; 5: 订单完成;
     */
    public static class OrderStatus {
        public static final int ORDER_STATUS_PAY_PROCESSING = 0; // 订单支付中
        public static final int ORDER_STATUS_PAY_SUCCESS = 1; // 支付成功，商家待接单
        public static final int ORDER_STATUS_PAY_FAIL = 2; // 支付失败
        public static final int ORDER_STATUS_SELLER_PROCESSING = 3; // 商家已接单，正在出餐
        public static final int ORDER_STATUS_CANCEL = 4; // 取消订单
        public static final int ORDER_STATUS_SUCCESS = 5; // 订单完成
    }

    public static class OperationType {
        public static final int OPERATION_PAY_PROCESSING = 0; // 订单支付中 ===> 无
        public static final int OPERATION_PAY_SUCCESS = 1; // 支付成功，商家待接单 ===> 接单
        public static final int OPERATION_PAY_FAIL = 2; // 支付失败 ===> 无
        public static final int OPERATION_SELLER_PROCESSING = 3; // 商家已接单，正在出餐 ===> 已送达
        public static final int OPERATION_CANCEL = 4; // 取消订单 ===> 无
        public static final int OPERATION_SUCCESS = 5; // 订单完成 ===> 无
    }

    public static class QueueType {
        public static final int QUEUE_TYPE_ORDER_SELLER_PROCESSING = 0; // 定时接单
    }

}