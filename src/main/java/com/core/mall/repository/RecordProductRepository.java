package com.core.mall.repository;

import com.core.mall.model.entity.RecordProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordProductRepository extends JpaRepository<RecordProduct, Long> {

    RecordProduct findByProductId(String productId);

    List<RecordProduct> findByProductTypeId(Integer productTypeId);

}