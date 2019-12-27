package com.core.mall.service.core;

import com.core.mall.model.entity.TransOrder;

import java.util.Map;

public interface TransactionService {

    public Map<String, String> doTransOrder(TransOrder transOrder, String openId);

    public void payCallback(String globalOrderGid, String returnCode);

    public String orderQuery(String orderGid);
}
