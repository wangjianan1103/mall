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
public class TransOrder extends BaseEntity {
    @Column(unique = true, nullable = false, length = 36)
    private String gid;
    @Column(nullable = false, length = 36)
    private String userGid;
    @Column(nullable = false)
    private BigDecimal orderAmount = BigDecimal.ZERO;
    @Column(nullable = false)
    private Integer orderStatus = 0;
    @Column(length = 36)
    private String prePayId;
    @Column(length = 36)
    private String globalOrderId;
    @Column(length = 64)
    private String returnCode;
    @Column(length = 512)
    private String failReason;
    @Column(length = 64)
    private String remark;

}