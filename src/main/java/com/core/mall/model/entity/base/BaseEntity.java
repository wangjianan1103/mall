package com.core.mall.model.entity.base;

import lombok.Data;

import javax.persistence.*;

@Data
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private long createTime;
    @Column(nullable = false)
    private long updateTime;
}
