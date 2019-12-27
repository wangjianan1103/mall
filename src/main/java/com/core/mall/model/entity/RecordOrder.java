package com.core.mall.model.entity;

import com.core.mall.model.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@DynamicInsert
@Entity
public class RecordOrder extends BaseEntity {
    @Column(unique = true, nullable = false, length = 36)
    private String gid;
    @Column(nullable = false, length = 36)
    private String userGid;
    @Column(nullable = false, length = 36)
    private String addressGid;
    @Column(length = 36)
    private String orderGid;
    @Column()
    private String transGid;
    @Column()
    private BigDecimal orderAmount = BigDecimal.ZERO;
    @Column()
    private BigDecimal fareAmount = BigDecimal.ZERO;
    @Column()
    private Integer orderStatus = 0;
    @Column()
    private Integer dayOrder = 0;
    @Column()
    private String orderDesc;

}