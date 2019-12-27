package com.core.mall.service.core;


import com.core.mall.model.entity.RecordOrder;
import com.core.mall.model.entity.RecordProductOrder;
import com.core.mall.model.entity.TransOrder;
import com.core.mall.model.entity.UtilVendorAsyncTask;

import java.util.List;

public interface BizOrderService {

    void doRecordOrder(RecordOrder recordOrder, List<RecordProductOrder> orderList, TransOrder transOrder, UtilVendorAsyncTask task);

}
