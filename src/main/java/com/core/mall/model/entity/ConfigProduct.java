package com.core.mall.model.entity;

import com.core.mall.model.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
public class ConfigProduct extends BaseEntity {
    @Column()
    private Integer productTypeId;
    @Column(nullable = false, unique = true, length = 64)
    private String name = "";
    @Column()
    private Integer type = -1;
    @Column()
    private Double sequence = 1.0;
    @Column()
    private String ico;
    @Column()
    private Boolean isValid = Boolean.TRUE;

}
