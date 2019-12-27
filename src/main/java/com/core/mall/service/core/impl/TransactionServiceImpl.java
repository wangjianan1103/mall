package com.core.mall.service.core.impl;

import com.core.mall.config.CoreException;
import com.core.mall.enums.ConfigGlobalEnum;
import com.core.mall.enums.ErrorCodeEnum;
import com.core.mall.model.entity.RecordOrder;
import com.core.mall.model.entity.TransOrder;
import com.core.mall.repository.ConfigGlobalRepository;
import com.core.mall.repository.RecordOrderRepository;
import com.core.mall.repository.TransOrderRepository;
import com.core.mall.service.core.RiskMessageService;
import com.core.mall.service.core.TransactionService;
import com.core.mall.util.GlobalConstants;
import com.core.mall.util.Utility;
import com.core.wxpay.config.IWxPayConfig;
import com.core.wxpay.sdk.WXPay;
import com.core.wxpay.sdk.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.core.wxpay.sdk.WXPayConstants.SUCCESS;

@Service
public class TransactionServiceImpl implements TransactionService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${vendor.wx.config.callback}")
    private String callBack;
    @Autowired
    private IWxPayConfig iWxPayConfig;

    @Autowired
    private ConfigGlobalRepository configGlobalRepository;

    @Autowired
    private TransOrderRepository transOrderRepository;

    @Autowired
    private RecordOrderRepository recordOrderRepository;

    @Autowired
    private RiskMessageService riskMessageService;

    @Override
    public Map<String, String>  doTransOrder(TransOrder transOrder, String openId) {
        logger.info("doTransOrder start, transOrder={}", transOrder);
        if (transOrder == null || openId == null) {
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }
        // 发起微信支付
        WXPay wxpay = null;
        Map<String, String> result = new HashMap<>();
        try {
            wxpay = new WXPay(iWxPayConfig);

            Map<String, String> data = new HashMap<String, String>();
            data.put("body", "恋家披萨外卖");
            data.put("out_trade_no", transOrder.getGlobalOrderId());
            data.put("total_fee", String.valueOf(transOrder.getOrderAmount().multiply(new BigDecimal(100)).intValue()));
            data.put("spbill_create_ip", "192.168.31.166");
            data.put("openid", openId);
            data.put("notify_url", "https://api.lianjia.oopmind.com/payCallback");
            data.put("trade_type", "JSAPI");
            data.put("time_expire", Utility.forMartTime(Long.parseLong(String.valueOf(Utility.getCurrentTimeStamp() + 700)) * 1000, "yyyyMMddHHmmss"));

            logger.info("发起微信支付下单接口, request={}", data);
            Map<String, String> response = wxpay.unifiedOrder(data);
            logger.info("微信支付下单成功, 返回值 response={}", response);
            String returnCode = response.get("return_code");
            if (!SUCCESS.equals(returnCode)) {
                return null;
            }
            String resultCode = response.get("result_code");
            if (!SUCCESS.equals(resultCode)) {
                return null;
            }
            String prepay_id = response.get("prepay_id");
            if (prepay_id == null) {
                return null;
            }

            TransOrder updateOrder = new TransOrder();
            updateOrder.setUpdateTime(Utility.getCurrentTimeStamp());
            updateOrder.setPrePayId(prepay_id);
            updateOrder.setId(transOrder.getId());
            updateOrder.setGid(transOrder.getGid());
            transOrderRepository.saveAndFlush(updateOrder);

            String packages = "prepay_id=" + prepay_id;
            Map<String, String> wxPayMap = new HashMap<String, String>();
            wxPayMap.put("appId", iWxPayConfig.getAppID());
            wxPayMap.put("timeStamp", String.valueOf(Utility.getCurrentTimeStamp()));
            wxPayMap.put("nonceStr", Utility.generateUUID());
            wxPayMap.put("package", packages);
            wxPayMap.put("signType", "MD5");
            String sign = WXPayUtil.generateSignature(wxPayMap, iWxPayConfig.getKey());

            result.put("prepay_id", prepay_id);
            result.put("sign", sign);
            result.putAll(wxPayMap);
            return result;
        } catch (Exception e) {
            logger.error("doTransOrder error", e);
        }
        return null;
    }

    @Override
    public void payCallback(String globalOrderGid, String returnCode) {
        if (globalOrderGid == null) {
            return;
        }

        TransOrder transOrder = transOrderRepository.findByGlobalOrderId(globalOrderGid);
        if (transOrder == null || transOrder.getOrderStatus() == GlobalConstants.TransOrderStatus.TRANS_ORDER_STATUS_PAY_SUCCESS
                || transOrder.getOrderStatus() == GlobalConstants.TransOrderStatus.TRANS_ORDER_STATUS_PAY_FAIL) {
            return;
        }

        String userGid = transOrder.getUserGid();
        String gid = transOrder.getGid();

        RecordOrder recordOrder = recordOrderRepository.findByGid(gid);
        if (recordOrder == null) {
            return;
        }

        int status = convertRecordOrderStatus(returnCode);

        boolean isSuccess = false;
        if (GlobalConstants.TransOrderStatus.TRANS_ORDER_STATUS_PAY_SUCCESS == status) {
            isSuccess = true;
        }

        TransOrder updateTransOrder = new TransOrder();
        updateTransOrder.setUpdateTime(Utility.getCurrentTimeStamp());
        updateTransOrder.setReturnCode(returnCode);
        updateTransOrder.setOrderStatus(status);
        updateTransOrder.setGid(transOrder.getGid());
        updateTransOrder.setId(transOrder.getId());

        RecordOrder updateRecordOrder = new RecordOrder();
        updateRecordOrder.setId(recordOrder.getId());
        updateRecordOrder.setUpdateTime(Utility.getCurrentTimeStamp());
        updateRecordOrder.setOrderStatus(status);
        if (isSuccess) {
            updateRecordOrder.setDayOrder(0);
        }

        transOrderRepository.saveAndFlush(updateTransOrder);
        recordOrderRepository.saveAndFlush(updateRecordOrder);

        // 推送消息
        if (isSuccess) {
            // 推送新订单消息给管理员
            riskMessageService.sendNewOrderWeChatMessage(userGid, recordOrder);

            // 给用户推送购买成功消息
            riskMessageService.sendOrderSuccess(userGid, recordOrder);

            // 添加定时任务，默认接单
            String time = configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_DEFAULT_SELLER_ORDER_TIME.getValue());
//            DelayQueueServiceImpl.Queue queue = new DelayQueueServiceImpl.Queue(recordOrder.getGid(), Integer.parseInt(time), GlobalConstants.QueueType.QUEUE_TYPE_ORDER_SELLER_PROCESSING);
//            delayQueueService.push(queue);
        }
    }

    @Override
    public String orderQuery(String orderGid) {
        logger.info("orderQuery start, orderGid={}", orderGid);
        if (orderGid == null) {
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }
        TransOrder transOrder = transOrderRepository.findByGid(orderGid);
        if (transOrder == null) {
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        String transStatus = "";

        // 发起微信支付
        WXPay wxpay = null;
        Map<String, String> result = new HashMap<>();
        try {
            wxpay = new WXPay(iWxPayConfig);

            Map<String, String> data = new HashMap<String, String>();
            data.put("out_trade_no", transOrder.getGlobalOrderId());
            data.put("nonce_str", Utility.generateUUID());

            logger.info("发起查询微信支付结果接口, request={}", data);
            Map<String, String> response = wxpay.orderQuery(data);
            logger.info("查询微信支付下单成功, 返回值 response={}", response);
            String returnCode = response.get("return_code");
            if (!SUCCESS.equals(returnCode)) {
                return transStatus;
            }

            String resultCode = response.get("result_code");
            if (!SUCCESS.equals(resultCode)) {
                return transStatus;
            }

            String tradeState = response.get("trade_state");
            switch (tradeState) {
                case GlobalConstants.TradeState.TRADE_STATE_SUCCESS:
                    transStatus = "SUCCESS";
                    break;
                case GlobalConstants.TradeState.TRADE_STATE_CLOSED:
                case GlobalConstants.TradeState.TRADE_STATE_REVOKED:
                case GlobalConstants.TradeState.TRADE_STATE_PAYERROR:
                    transStatus = "FAIL";
                    break;
                case GlobalConstants.TradeState.TRADE_STATE_REFUND:
                    transStatus = "FAIL";
                    break;
                case GlobalConstants.TradeState.TRADE_STATE_NOTPAY:
                    // 订单超过10分钟未支付，取消订单
                    if (transOrder.getCreateTime() < Utility.getCurrentTimeStamp() - 600) {
                        int status = this.cancelOrder(transOrder);
                        if (status == 1) {
                            transStatus = "FAIL";
                        }
                    }
                    break;
                case GlobalConstants.TradeState.TRADE_STATE_USERPAYING:
                    break;
                default:
                    break;
            }

            if ("SUCCESS".equals(transStatus)) {
                String totalFee = response.get("total_fee");
                if (transOrder.getOrderAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(totalFee)) != 0) {
                    logger.info("orderQuery totalFee is error, orderGid={}", orderGid);
                    return null;
                }
                this.payCallback(transOrder.getGlobalOrderId(), transStatus);
            } else if ("FAIL".equals(transStatus)) {
                this.payCallback(transOrder.getGlobalOrderId(), transStatus);
            } else {
                return transStatus;
            }
        } catch (Exception e) {
            logger.info("orderQuery Exception is error, e={}", e);
        }

        return transStatus;
    }

    private int cancelOrder(TransOrder transOrder) {
        int resultFlag = 0;
        WXPay wxpay = null;
        Map<String, String> result = new HashMap<>();
        try {
            wxpay = new WXPay(iWxPayConfig);

            Map<String, String> data = new HashMap<String, String>();
            data.put("out_trade_no", transOrder.getGlobalOrderId());
            data.put("nonce_str", Utility.generateUUID());

            logger.info("发起取消支付订单接口, request={}", data);
            Map<String, String> response = wxpay.closeOrder(data);
            logger.info("取消微信支付订单成功, 返回值 response={}", response);
            String returnCode = response.get("return_code");
            if (!SUCCESS.equals(returnCode)) {
                return resultFlag;
            }

            String resultCode = response.get("result_code");
            if (!SUCCESS.equals(resultCode)) {
                return resultFlag;
            }

            // 取消成功
            this.payCallback(transOrder.getGlobalOrderId(), "FAIL");
            resultFlag = 1;
        } catch (Exception e) {
            logger.info("orderQuery Exception is error, e={}", e);
        }
        return resultFlag;
    }

    private int convertRecordOrderStatus(String returnCode){
        switch (returnCode) {
            case "SUCCESS":
                return GlobalConstants.TransOrderStatus.TRANS_ORDER_STATUS_PAY_SUCCESS;
            case "FAIL":
                return GlobalConstants.TransOrderStatus.TRANS_ORDER_STATUS_PAY_FAIL;
            default:
                return GlobalConstants.TransOrderStatus.TRANS_ORDER_STATUS_PAY_PROCESSING;
        }
    }
}
