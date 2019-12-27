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
public class RecordRating extends BaseEntity {
    @Column(nullable = false, length = 36)
    private String userGid;
    @Column(length = 36)
    private String orderGid;
    @Column(nullable = false)
    private Integer deliveryTime = 20;
    @Column(nullable = false)
    private BigDecimal score = BigDecimal.ZERO;
    @Column(nullable = false)
    private Integer rateType = 0;
    @Column()
    private String rateDesc;
    @Column(nullable = false)
    private Integer isValid = 1;

}