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
public class UserAddress extends BaseEntity {
    @Column(unique = true, nullable = false, length = 36)
    private String gid;
    @Column(nullable = false, length = 36)
    private String userGid;
    @Column()
    private String name;
    @Column(nullable = false)
    private Integer sex = 0;
    @Column()
    private String mobile;
    @Column()
    private String address;
    @Column(nullable = false)
    private Integer tag = 0;
    @Column(nullable = false)
    private Boolean isValid = Boolean.TRUE;

}