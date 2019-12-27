package com.core.mall.repository;

import com.core.mall.model.entity.RecordProductSpec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordProductSpecRepository extends JpaRepository<RecordProductSpec, Long> {

    List<RecordProductSpec> findByProductId(String productId);

    RecordProductSpec findById(long id);

}