package com.core.mall.util;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utility {

    private static int debugCurrentTimeStamp = 0;

    public static int getCurrentTimeStamp() {
        if (debugCurrentTimeStamp == 0) {
            return (int) (System.currentTimeMillis() / 1000);
        } else {
            return debugCurrentTimeStamp;
        }
    }

    public static String fmtYmdHms(int timestamp) {
        long ts = ((long) timestamp) * 1000;
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sf.format(ts);
    }

    public static String getForMartTime() {
        return forMartTime(String.valueOf(System.currentTimeMillis()), "yyyy-MM-dd");
    }

    public static String forMartTime(String timePoint) {
        return forMartTime(timePoint, "yyyy-MM-dd HH:mm:ss S");
    }

    public static String forMartTime(String timePoint, String pattern) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(Long.parseLong(timePoint));
    }

    public static String forMartTime(Long timePoint, String pattern) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        String a = sf.format(Long.parseLong(String.valueOf(timePoint)));
        return sf.format(Long.parseLong(String.valueOf(timePoint)));
    }

    public static int getTimeStampByDay(int day) {
        return getDayStartTime(getCurrentTimeStamp() - day * 86400);
    }

    public static int getDayStartTime(int timePoint) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        long theTime = ((long) timePoint) * 1000;
        calendar.setTimeInMillis(theTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        calendar.clear();
        calendar.set(year, month, day, 0, 0, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generateGlobalOrder() {
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return (1 + String.format("%015d", hashCodeV));
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return (!isBlank(str));
    }

    public static String encodeUtf8(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        return url;
    }

    public static String getMd5(String key) {
        if (Utility.isBlank(key)) {
            return key;
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(key.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(md5.digest());
        } catch (Exception e) {
        }
        return key;
    }

    public static String getCashKey(String key) {
        return "core:cash:" + key;
    }

    public static String getLockerKey(String key) {
        return "core:locker:" + key;
    }


    /**
     *
     * @param orderStatus record_order_status 订单状态
     * @return status 返回订单显示状态
     * 0: 订单支付中;
     * 1: 支付成功，商家待接单;
     * 2: 支付失败;
     * 3: 商家已接单，正在出餐;
     * 4: 取消订单;
     * 5: 订单完成;
     */
    public static int convertRecordOrderStatus(int orderStatus) {
        switch (orderStatus) {
            case GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_PAY_PROCESSING: {
                return GlobalConstants.OrderStatus.ORDER_STATUS_PAY_PROCESSING;
            }
            case GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_PAY_SUCCESS: {
                return GlobalConstants.OrderStatus.ORDER_STATUS_PAY_SUCCESS;
            }
            case GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_PAY_FAIL: {
                return GlobalConstants.OrderStatus.ORDER_STATUS_PAY_FAIL;
            }
            case GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_SELLER_PROCESSING:
            case GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_SELLER_SEND:
            case GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_SELLER_SEND_SUCCESS: {
                return GlobalConstants.OrderStatus.ORDER_STATUS_SELLER_PROCESSING;
            }
            case GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_CANCEL: {
                return GlobalConstants.OrderStatus.ORDER_STATUS_CANCEL;
            }
            case GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_SUCCESS: {
                return GlobalConstants.OrderStatus.ORDER_STATUS_SUCCESS;
            }
            default: {
                return GlobalConstants.OrderStatus.ORDER_STATUS_PAY_PROCESSING;
            }
        }
    }

    /**
     * @param status 页面显示订单的状态
     * @return status 返回订单状态 record_order_status
     * <p>
     * in  页面显示订单状态; 0: 待支付; 1: 待接单; 2: 支付失败; 3: 正在出餐;  4: 取消订单; 5: 订单完成;
     * out 订单记录显示状态: 0: 订单支付中; 1: 支付成功; 2: 支付失败; 3: 商家接单; 4: 商家已发货; 5: 送货成功; 6: 取消订单; 7: 订单完成;
     */
    public static List<Integer> convertOrderStatus(int status) {
        List<Integer> convertStatus = new ArrayList<>();

        switch (status) {
            case GlobalConstants.OrderStatus.ORDER_STATUS_PAY_PROCESSING: {
                convertStatus.add(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_PAY_PROCESSING);
                break;
            }
            case GlobalConstants.OrderStatus.ORDER_STATUS_PAY_SUCCESS: {
                convertStatus.add(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_PAY_SUCCESS);
                break;
            }
            case GlobalConstants.OrderStatus.ORDER_STATUS_PAY_FAIL: {
                convertStatus.add(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_PAY_FAIL);
                break;
            }
            case GlobalConstants.OrderStatus.ORDER_STATUS_SELLER_PROCESSING: {
                convertStatus.add(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_SELLER_PROCESSING);
                convertStatus.add(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_SELLER_SEND);
                convertStatus.add(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_SELLER_SEND_SUCCESS);
                break;
            }
            case GlobalConstants.OrderStatus.ORDER_STATUS_CANCEL: {
                convertStatus.add(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_CANCEL);
                break;
            }
            case GlobalConstants.OrderStatus.ORDER_STATUS_SUCCESS: {
                convertStatus.add(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_SUCCESS);
                break;
            }
            default: {
                convertStatus.add(GlobalConstants.OrderStatus.ORDER_STATUS_PAY_PROCESSING);
                break;
            }
        }
        return convertStatus;
    }

    public static int converSubcribeWay(String way) {
        switch (way) {
            case "ADD_SCENE_SEARCH": {
                return GlobalConstants.SubscribeWay.SUBSCRIBE_WAY_ADD_SCENE_SEARCH;
            }
            case "ADD_SCENE_ACCOUNT_MIGRATION": {
                return GlobalConstants.SubscribeWay.SUBSCRIBE_WAY_ADD_SCENE_ACCOUNT_MIGRATION;
            }
            case "ADD_SCENE_PROFILE_CARD": {
                return GlobalConstants.SubscribeWay.SUBSCRIBE_WAY_ADD_SCENE_PROFILE_CARD;
            }
            case "ADD_SCENE_QR_CODE": {
                return GlobalConstants.SubscribeWay.SUBSCRIBE_WAY_ADD_SCENE_QR_CODE;
            }
            case "ADD_SCENEPROFILE_LINK": {
                return GlobalConstants.SubscribeWay.SUBSCRIBE_WAY_ADD_SCENEPROFILE_LINK;
            }
            case "ADD_SCENE_PROFILE_ITEM": {
                return GlobalConstants.SubscribeWay.SUBSCRIBE_WAY_ADD_SCENE_PROFILE_ITEM;
            }
            case "ADD_SCENE_PAID": {
                return GlobalConstants.SubscribeWay.SUBSCRIBE_WAY_ADD_SCENE_PAID;
            }
            case "ADD_SCENE_OTHERS": {
                return GlobalConstants.SubscribeWay.SUBSCRIBE_WAY_ADD_SCENE_OTHERS;
            }
            default: {
                return GlobalConstants.SubscribeWay.SUBSCRIBE_WAY_DEFAULT;
            }
        }
    }
}
