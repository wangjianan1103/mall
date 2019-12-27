package com.core.mall.repository;

import com.core.mall.model.entity.TransOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransOrderRepository extends JpaRepository<TransOrder, Long> {

    TransOrder findByGid(String gid);

    TransOrder findByGlobalOrderId(String globalOrderId);
}