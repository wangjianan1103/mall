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
public class RecordProductSpec extends BaseEntity {
    @Column(nullable = false, length = 36)
    private String productId;
    @Column(nullable = false, length = 64)
    private String name;
    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;
    @Column(nullable = false)
    private String description = "";
    @Column(nullable = false)
    private Boolean isValid = Boolean.TRUE;

}