package com.core.mall.repository;

import com.core.mall.model.entity.RecordProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordProductOrderRepository extends JpaRepository<RecordProductOrder, Long> {

    List<RecordProductOrder> findByOrderGid(String orderGid);
}