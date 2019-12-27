package com.core.mall.model.entity;

import com.core.mall.model.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
public class RecordProductOrder extends BaseEntity {
    @Column(unique = true, nullable = false, length = 36)
    private String gid;
    @Column(nullable = false, length = 36)
    private String productGid;
    @Column(nullable = false, length = 36)
    private String orderGid;
    @Column(nullable = false)
    private Integer specId = 0;
    @Column(nullable = false)
    private Integer specType = 0;
    @Column(nullable = false)
    private Integer count = 0;
    @Column(nullable = false)
    private BigDecimal priceAmount = BigDecimal.ZERO;

}