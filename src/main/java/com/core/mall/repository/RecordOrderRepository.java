package com.core.mall.repository;

import com.core.mall.model.entity.RecordOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordOrderRepository extends JpaRepository<RecordOrder, Long> {

    RecordOrder findByGid(String gid);

    List<RecordOrder> findByUserGidAndCreateTimeGreaterThanAndOrderStatusNot(String userGid, Long createTime, Integer orderStatus);
}
