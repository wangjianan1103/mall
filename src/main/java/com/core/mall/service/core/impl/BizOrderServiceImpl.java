package com.core.mall.service.core.impl;

import com.core.mall.model.entity.RecordOrder;
import com.core.mall.model.entity.RecordProductOrder;
import com.core.mall.model.entity.TransOrder;
import com.core.mall.model.entity.UtilVendorAsyncTask;
import com.core.mall.repository.RecordOrderRepository;
import com.core.mall.repository.RecordProductOrderRepository;
import com.core.mall.repository.TransOrderRepository;
import com.core.mall.repository.UtilVendorAsyncTaskRepository;
import com.core.mall.service.core.BizOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作数据库
 */
@Service
public class BizOrderServiceImpl implements BizOrderService {

    @Autowired
    private RecordOrderRepository recordOrderRepository;

    @Autowired
    private RecordProductOrderRepository recordProductOrderRepository;

    @Autowired
    private TransOrderRepository transOrderRepository;

    @Autowired
    private UtilVendorAsyncTaskRepository utilVendorAsyncTaskRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = {Exception.class})
    public void doRecordOrder(RecordOrder recordOrder, List<RecordProductOrder> orderList,
                              TransOrder transOrder, UtilVendorAsyncTask task) {
        recordOrderRepository.saveAndFlush(recordOrder);
        transOrderRepository.saveAndFlush(transOrder);
        utilVendorAsyncTaskRepository.save(task);

        if (orderList != null && orderList.size() > 0) {
            for (RecordProductOrder order : orderList) {
                recordProductOrderRepository.saveAndFlush(order);
            }
        }

    }
}
