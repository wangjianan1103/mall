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
public class UserWeChatInfo extends BaseEntity {
    @Column(unique = true, nullable = false, length = 36)
    private String userGid;
    @Column(nullable = false, length = 36)
    private String openId = "";
    @Column(nullable = false, length = 36)
    private String unionId = "";
    @Column(nullable = false)
    private Integer subscribeStatus = 0;
    @Column(nullable = false)
    private Integer subscribeWay = 0;
    @Column()
    private String nickName;
    @Column(nullable = false)
    private Integer sex = 0;
    @Column(nullable = false)
    private String city = "";
    @Column(nullable = false)
    private String province = "";
    @Column(nullable = false)
    private String country = "";
    @Column(nullable = false)
    private String headImgUrl = "";
}