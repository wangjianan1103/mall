package com.core.mall.service.core;


import com.core.mall.model.entity.RecordOrder;
import com.core.mall.model.params.*;

public interface OrderService {

    OrderDoInfoResp doOrder(OrderDoInfoParam req);

    OrderStatusResp getOrderStatus(OrderStatusParam req);

    OrderListResp orderList(OrderListParam req);

    /**
     * 获取订单详情
     */
    OrderDetailResp orderDetail(OrderDetailParam req);

    void addTaskOrderStatus(RecordOrder recordOrder);

    /**
     * 修改订单状态为完成
     *
     * @param orderGid 订单gid
     */
    void doOrderSuccess(String orderGid);

    void doReceiptOrder(String orderGid);

}
