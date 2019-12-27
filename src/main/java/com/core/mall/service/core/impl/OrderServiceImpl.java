package com.core.mall.service.core.impl;

import com.core.mall.config.CoreException;
import com.core.mall.enums.ConfigGlobalEnum;
import com.core.mall.enums.ErrorCodeEnum;
import com.core.mall.model.entity.*;
import com.core.mall.model.params.*;
import com.core.mall.repository.*;
import com.core.mall.service.core.BizOrderService;
import com.core.mall.service.core.OrderService;
import com.core.mall.service.core.RiskMessageService;
import com.core.mall.service.core.TransactionService;
import com.core.mall.util.GlobalConstants;
import com.core.mall.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    private final static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public final static String DEFAULT_USER_GID = "default_user_gid";

    @Autowired
    private UserBaseRepository userBaseRepository;
    @Autowired
    private RecordProductRepository recordProductRepository;
    @Autowired
    private RecordOrderRepository recordOrderRepository;
    @Autowired
    private RecordProductOrderRepository recordProductOrderRepository;
    @Autowired
    private TransOrderRepository transOrderRepository;
    @Autowired
    private UserWeChatInfoRepository userWeChatInfoRepository;
    @Autowired
    private ConfigGlobalRepository configGlobalRepository;
    @Autowired
    private UserAddressRepository userAddressRepository;
    @Autowired
    private UtilVendorAsyncTaskRepository vendorAsyncTaskRepository;
    @Autowired
    private RecordProductSpecRepository recordProductSpecRepository;
    @Autowired
    private RiskMessageService riskMessageService;
    @Autowired
    private BizOrderService bizOrderService;
    @Autowired
    private TransactionService transactionService;

    @Override
    public OrderDoInfoResp doOrder(OrderDoInfoParam req) {
        logger.info("doOrder() start. req={}", req);
        if (req == null) {
            logger.error("doOrder() req is null");
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR.getErrorCode());
        }

        String userGid = req.getUserGid();
        String addressGid = req.getAddressGid();
        List<OrderDoInfoParam.OrderProduct> orderProductList = req.getOrderProductList();
        if (userGid == null || orderProductList == null || orderProductList.size() <= 0) {
            logger.error("doOrder() userGid or orderProductList is null, userGid={}", userGid);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR.getErrorCode());
        }

        UserBase userBase = userBaseRepository.findByGid(userGid);
        if (userBase == null) {
            logger.error("doOrder() userBase is null, userGid={}", userGid);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        UserWeChatInfo userWeChatInfo = userWeChatInfoRepository.findByUserGid(userGid);
        if (userWeChatInfo == null) {
            logger.error("doOrder() userWeChatInfo is null, userGid={}", userGid);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        List<RecordProductOrder> orderList = new ArrayList<>();
        String orderGid = Utility.generateUUID();
        int currentTimeStamp = Utility.getCurrentTimeStamp();
        BigDecimal orderAmount = BigDecimal.ZERO;

        for (OrderDoInfoParam.OrderProduct orderProduct : orderProductList) {
            String productGid = orderProduct.getProductGid();
            int count = orderProduct.getCount();
            if (count <= 0) {
                break;
            }

            RecordProduct product = recordProductRepository.findByProductId(productGid);

            // 规格类产品
            if (product.getSpecType() == GlobalConstants.RecordProductSpec.RECORD_PRODUCT_SPEC_SUM) {
                List<OrderDoInfoParam.OrderSpec> specList = orderProduct.getOrderSpecList();
                if (specList == null || specList.size() <= 0) {
                    throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
                }

                for (OrderDoInfoParam.OrderSpec orderSpec : specList) {
                    int specId = orderSpec.getSpecId();
                    int specCount = orderSpec.getCount();

                    RecordProductSpec recordProductSpec = recordProductSpecRepository.findById(specId);
                    if (recordProductSpec == null) {
                        throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
                    }

                    BigDecimal price = recordProductSpec.getPrice();
                    BigDecimal amount = price.multiply(new BigDecimal(specCount)).setScale(2, BigDecimal.ROUND_DOWN);
                    orderAmount = orderAmount.add(amount);

                    RecordProductOrder order = new RecordProductOrder();
                    order.setCreateTime(currentTimeStamp);
                    order.setUpdateTime(currentTimeStamp);
                    order.setCount(specCount);
                    order.setGid(Utility.generateUUID());
                    order.setOrderGid(orderGid);
                    order.setPriceAmount(amount);
                    order.setProductGid(productGid);
                    order.setSpecId(specId);
                    order.setSpecType(GlobalConstants.RecordProductSpec.RECORD_PRODUCT_SPEC_SUM);
                    orderList.add(order);
                }
            } else {
                BigDecimal price = product.getPrice();
                BigDecimal amount = price.multiply(new BigDecimal(count)).setScale(2, BigDecimal.ROUND_DOWN);
                orderAmount = orderAmount.add(amount);

                RecordProductOrder order = new RecordProductOrder();
                order.setCreateTime(currentTimeStamp);
                order.setUpdateTime(currentTimeStamp);
                order.setCount(count);
                order.setGid(Utility.generateUUID());
                order.setOrderGid(orderGid);
                order.setPriceAmount(amount);
                order.setProductGid(productGid);
                order.setSpecId(0);
                order.setSpecType(GlobalConstants.RecordProductSpec.RECORD_PRODUCT_SPEC_SIMPLE);
                orderList.add(order);
            }
        }

        // 计算快递费，满多少钱减免快递费
        String deliveryPrice = configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_GLOBAL_KEY_DELIVERY_PRICE.getValue());
        String sumDeliveryPrice = configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_GLOBAL_KEY_SUM_DELIVERY_PRICE.getValue());
        if (deliveryPrice == null || sumDeliveryPrice == null) {
            logger.error("doOrder() deliveryPrice or minPrice is null, userGid={}", userGid);
            throw new CoreException(ErrorCodeEnum.SYS_FAIL);
        }

        if (orderAmount.compareTo(new BigDecimal(sumDeliveryPrice)) >= 0) {
            // 订单金额大于满减金额，快递费为0
            deliveryPrice = "0";
        }

        // 计算快递费
        orderAmount = orderAmount.add(new BigDecimal(deliveryPrice));

        String transOrderGid = Utility.generateUUID();
        RecordOrder recordOrder = new RecordOrder();
        recordOrder.setCreateTime(currentTimeStamp);
        recordOrder.setUpdateTime(currentTimeStamp);
        recordOrder.setGid(Utility.generateUUID());
        recordOrder.setUserGid(userGid);
        recordOrder.setOrderGid(orderGid);
        recordOrder.setOrderAmount(orderAmount);
        recordOrder.setAddressGid(addressGid);
        recordOrder.setTransGid(transOrderGid);
        recordOrder.setFareAmount(new BigDecimal(deliveryPrice));
        recordOrder.setOrderDesc(req.getOrderDesc());
        recordOrder.setDayOrder(1);
        recordOrder.setOrderStatus(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_PAY_PROCESSING);

        TransOrder transOrder = new TransOrder();
        transOrder.setCreateTime(currentTimeStamp);
        transOrder.setUpdateTime(currentTimeStamp);
        transOrder.setGid(transOrderGid);
        transOrder.setOrderAmount(orderAmount);
        transOrder.setUserGid(userGid);
        transOrder.setOrderStatus(GlobalConstants.TransOrderStatus.TRANS_ORDER_STATUS_PAY_PROCESSING);
        transOrder.setGlobalOrderId(Utility.generateGlobalOrder());

        UtilVendorAsyncTask task = new UtilVendorAsyncTask();
        task.setOrderId(transOrderGid);
        task.setStatus(UtilVendorAsyncTask.STATUS_UNKNOWN);
        task.setCreateTime(Utility.getCurrentTimeStamp());
        task.setUpdateTime(Utility.getCurrentTimeStamp());
        task.setTask((short) UtilVendorAsyncTask.TASK_WE_CHAT);
        task.setNextTime(Utility.getCurrentTimeStamp() + 10);
        task.setRetryNum(0);

        bizOrderService.doRecordOrder(recordOrder, orderList, transOrder, task);

        Map<String, String > resultMap = transactionService.doTransOrder(transOrder, userWeChatInfo.getOpenId());

        OrderDoInfoResp response = new OrderDoInfoResp();
        response.setRecordOrderGid(recordOrder.getGid());
        response.setResultMap(resultMap);
        logger.info("doOrder() end. response={}", response);
        return response;
    }

    @Override
    public OrderStatusResp getOrderStatus(OrderStatusParam param) {
        logger.info("getOrderStatus() start. param={}", param);
        if (param == null) {
            logger.error("getOrderStatus() req is null");
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        String userGid = param.getUserGid();
        String recordOrderGid = param.getRecordOrderGid();
        if (userGid == null || recordOrderGid == null) {
            logger.error("getOrderStatus() userGid or recordOrderGid is null, userGid={}", userGid);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        UserBase userBase = userBaseRepository.findByGid(userGid);
        if (userBase == null) {
            logger.error("getOrderStatus() userBase is null, userGid={}", userGid);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        RecordOrder recordOrder = recordOrderRepository.findByGid(recordOrderGid);
        if (recordOrder == null) {
            logger.error("getOrderStatus() recordOrder is null, userGid={}", userGid);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        OrderStatusResp response = new OrderStatusResp();
        response.setRecordOrderGid(recordOrderGid);
        response.setStatus(Utility.convertRecordOrderStatus(recordOrder.getOrderStatus()));
        response.setOrderAmount(recordOrder.getOrderAmount());

        logger.info("getOrderStatus() end. response={}", response);
        return response;
    }

    @Override
    public OrderListResp orderList(OrderListParam param) {
        logger.info("orderList() start. param={}", param);

        OrderListResp response = new OrderListResp();

        String userGid = param.getUserGid();
        if (userGid == null) {
            logger.error("orderList() userGid or recordOrderGid is null");
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        UserBase userBase = userBaseRepository.findByGid(userGid);
        if (userBase == null) {
            logger.error("getOrderStatus() userBase is null, userGid={}", userGid);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        List<OrderListResp.OrderInfo> orderInfoList = new ArrayList<>();

        List<RecordOrder> orderList = recordOrderRepository.findByUserGidAndCreateTimeGreaterThanAndOrderStatusNot(userGid, (long) (Utility.getCurrentTimeStamp() - 86400 * 15), GlobalConstants.OrderStatus.ORDER_STATUS_PAY_FAIL);
        if (orderList != null && orderList.size() > 0) {
            for (RecordOrder order : orderList) {
                List<OrderListResp.ProductOrderInfo> productOrderInfoList = new ArrayList<>();

                String orderGid = order.getOrderGid();
                List<RecordProductOrder> productOrderList = recordProductOrderRepository.findByOrderGid(orderGid);
                for (RecordProductOrder productOrder : productOrderList) {
                    OrderListResp.ProductOrderInfo productOrderInfo = new OrderListResp.ProductOrderInfo();

                    RecordProduct recordProduct = recordProductRepository.findByProductId(productOrder.getProductGid());
                    if (recordProduct == null) {
                        break;
                    }

                    if (productOrder.getSpecType() == GlobalConstants.RecordProductSpec.RECORD_PRODUCT_SPEC_SUM) {
                        int specId = productOrder.getSpecId();
                        RecordProductSpec recordProductSpec = recordProductSpecRepository.findById(specId);
                        productOrderInfo.setSpecType(productOrder.getSpecType());
                        productOrderInfo.setSpecName(recordProductSpec.getName());
                    }
                    productOrderInfo.setCount(productOrder.getCount());
                    productOrderInfo.setPriceAmount(productOrder.getPriceAmount());
                    productOrderInfo.setProductName(recordProduct.getName());
                    productOrderInfoList.add(productOrderInfo);
                }

                OrderListResp.OrderInfo orderInfo = new OrderListResp.OrderInfo();
                orderInfo.setGid(order.getGid());
                orderInfo.setStatus(Utility.convertRecordOrderStatus(order.getOrderStatus()));
                orderInfo.setOrderAmount(order.getOrderAmount());
                orderInfo.setDayOrder(order.getDayOrder());
                orderInfo.setProductOrderInfoList(productOrderInfoList);
                orderInfo.setName(configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_INDEX_SHOP_NAME.getValue()));
                orderInfo.setImageUrl(configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_INDEX_SHOP_IMG_URL.getValue()));
                orderInfoList.add(orderInfo);
            }
        }

        response.setOrderInfoList(orderInfoList);
        logger.info("orderList() end. response={}", response);
        return response;
    }

    @Override
    public OrderDetailResp orderDetail(OrderDetailParam param) {
        logger.info("orderDetail() start. param={}", param);
        OrderDetailResp response = new OrderDetailResp();

        if (param == null || param.getUserGid() == null || param.getRecordOrderGid() == null) {
            logger.info("orderDetail() error, userGid and orderGid is null. req={}", param);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        String userGid = param.getUserGid();
        String recordOrderGid = param.getRecordOrderGid();

        UserBase userBase = userBaseRepository.findByGid(userGid);
        if (userBase == null && !userGid.equals(DEFAULT_USER_GID)) {
            logger.error("orderDetail() userBase is null, userGid={}", userGid);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        RecordOrder recordOrder = recordOrderRepository.findByGid(recordOrderGid);
        String transGid = recordOrder.getTransGid();

        TransOrder transOrder = transOrderRepository.findByGid(transGid);
        if (transOrder == null) {
            logger.error("orderDetail() userBase is null, userGid={}", userGid);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        OrderDetailResp.OrderInfo orderInfo = new OrderDetailResp.OrderInfo();

        orderInfo.setCreateTime(recordOrder.getCreateTime());
        orderInfo.setEndTime(recordOrder.getUpdateTime());
        orderInfo.setFareAmount(recordOrder.getFareAmount());
        orderInfo.setGlobalOrderId(transOrder.getGlobalOrderId());
        orderInfo.setOrderAmount(recordOrder.getOrderAmount());
        orderInfo.setRecordOrderGid(recordOrderGid);
        orderInfo.setDesc(recordOrder.getOrderDesc());
        orderInfo.setStatus(Utility.convertRecordOrderStatus(recordOrder.getOrderStatus()));
        orderInfo.setShopAmount(recordOrder.getOrderAmount().subtract(recordOrder.getFareAmount()).setScale(2, BigDecimal.ROUND_DOWN));
        response.setOrderInfo(orderInfo);

        List<OrderDetailResp.Food> foods = new ArrayList<>();
        String orderGid = recordOrder.getOrderGid();
        List<RecordProductOrder> productOrderList = recordProductOrderRepository.findByOrderGid(orderGid);
        for (RecordProductOrder order : productOrderList) {
            OrderDetailResp.Food food = new OrderDetailResp.Food();

            RecordProduct recordProduct = recordProductRepository.findByProductId(order.getProductGid());
            if (recordProduct == null) {
                break;
            }

            if (order.getSpecType() == GlobalConstants.RecordProductSpec.RECORD_PRODUCT_SPEC_SUM) {
                int specId = order.getSpecId();
                RecordProductSpec recordProductSpec = recordProductSpecRepository.findById(specId);
                food.setSpecType(order.getSpecType());
                food.setSpecName(recordProductSpec.getName());
            }
            food.setCount(order.getCount());
            food.setPrice(order.getPriceAmount());
            food.setGid(recordProduct.getProductId());
            food.setIcon(recordProduct.getIcon());
            food.setName(recordProduct.getName());
            food.setDescription(recordProduct.getDescription());
            foods.add(food);
        }
        response.setFoodList(foods);

        String addressGid = recordOrder.getAddressGid();
        UserAddress userAddress = userAddressRepository.findByGid(addressGid);
        if (userAddress != null) {
            OrderDetailResp.Address address = new OrderDetailResp.Address();
            address.setGid(userAddress.getGid());
            address.setMobile(userAddress.getMobile());
            address.setName(userAddress.getName());
            address.setAddress(userAddress.getAddress());
            response.setAddress(address);
        }
        logger.info("orderDetail() end. response={}", response);
        return response;
    }

    @Override
    public void addTaskOrderStatus(RecordOrder recordOrder) {

        int nextTime = Integer.MAX_VALUE;
        ConfigGlobal configGlobal = configGlobalRepository.findByGlobalKey(ConfigGlobalEnum.CONFIG_GLOBAL_KEY_ORDER_STATUS_SUCCESS.getValue());
        if (configGlobal != null) {
            nextTime = Integer.parseInt(configGlobal.getGlobalValue());
        }
        UtilVendorAsyncTask task = new UtilVendorAsyncTask();
        task.setOrderId(recordOrder.getOrderGid());
        task.setStatus(UtilVendorAsyncTask.STATUS_UNKNOWN);
        task.setCreateTime(Utility.getCurrentTimeStamp());
        task.setUpdateTime(Utility.getCurrentTimeStamp());
        task.setTask((short) UtilVendorAsyncTask.TASK_ORDER_STATUS);
        task.setNextTime(nextTime);
        task.setRetryNum(0);
        vendorAsyncTaskRepository.save(task);
    }

    @Override
    public void doOrderSuccess(String orderGid) {

        if (orderGid == null) {
            return;
        }

        RecordOrder recordOrder = recordOrderRepository.findByGid(orderGid);
        if (recordOrder == null) {
            return;
        }

        String transGid = recordOrder.getTransGid();
        TransOrder transOrder = transOrderRepository.findByGid(transGid);
        if (transOrder == null) {
            return;
        }

        RecordOrder orderUpdate = new RecordOrder();
        orderUpdate.setId(recordOrder.getId());
        orderUpdate.setUpdateTime(Utility.getCurrentTimeStamp());
        orderUpdate.setOrderStatus(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_SUCCESS);

        bizOrderService.doRecordOrder(orderUpdate, null, null, null);
    }

    @Override
    public void doReceiptOrder(String orderGid) {

        RecordOrder recordOrder = recordOrderRepository.findByGid(orderGid);
        // 接单
        RecordOrder update = new RecordOrder();
        update.setId(recordOrder.getId());
        update.setUpdateTime(Utility.getCurrentTimeStamp());
        update.setOrderStatus(GlobalConstants.RecordOrderStatus.RECORD_ORDER_STATUS_SELLER_PROCESSING);

        recordOrderRepository.saveAndFlush(update);

        // 发送订单状态推送
        riskMessageService.sendOrderStatusSuccess(recordOrder.getUserGid(), recordOrder);
    }
}
