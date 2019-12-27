package com.core.mall.controller.content.api;

import com.core.mall.model.params.*;
import com.core.mall.service.core.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/order/do", method = RequestMethod.POST)
    public OrderDoInfoResp orderDo(@RequestBody OrderDoInfoParam param) {
        return orderService.doOrder(param);
    }

    @RequestMapping(value = "/order/status", method = RequestMethod.POST)
    public OrderStatusResp getOrderStatus(@RequestBody OrderStatusParam param) {
        return orderService.getOrderStatus(param);
    }

    @RequestMapping(value = "/order/list", method = RequestMethod.POST)
    public OrderListResp getOrderList(@RequestBody OrderListParam param) {
        return orderService.orderList(param);
    }

    @RequestMapping(value = "/order/detail", method = RequestMethod.POST)
    public OrderDetailResp orderDetail(@RequestBody OrderDetailParam param) {
        return orderService.orderDetail(param);
    }

}
