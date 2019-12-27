package com.core.mall.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@ToString(callSuper = true)
@Data
@Entity
public class ConfigPushMessage {

    @Id
    @GeneratedValue
    private long id;
    @Column(nullable = false, unique = true, length = 128)
    private String messageId;
    @Column(nullable = false, length = 32)
    private String messageType;
    @Column(nullable = false, length = 512)
    private String messageTitle;
    @Column(nullable = false, length = 512)
    private String messageText;
    @Column(nullable = false, length = 512)
    private String messageUrl;
    @Column(nullable = false, length = 512)
    private String picUrl;

}