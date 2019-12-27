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
public class ConfigGlobal extends BaseEntity {

    @Column(nullable = false, unique = true, length = 64)
    private String globalKey;
    @Column()
    private String globalValue;
    @Column()
    private String remark;

}
