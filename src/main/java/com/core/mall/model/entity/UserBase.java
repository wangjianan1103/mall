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
public class UserBase extends BaseEntity {
    @Column(unique = true, nullable = false, length = 36)
    private String gid;
    @Column()
    private String nickname;
    @Column()
    private String mobile;
    @Column()
    private String name;
    @Column()
    private String idCard;
    @Column()
    private String portraitUrl;
    @Column()
    private Integer userState = 0;
    @Column()
    private Integer pwdRetryCount = 0;
    @Column(length = 32)
    private String loginPwd;

}