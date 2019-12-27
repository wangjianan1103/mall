package com.core.mall.repository;

import com.core.mall.model.entity.ConfigPushMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigPushMessageRepository extends JpaRepository<ConfigPushMessage, Long> {

    ConfigPushMessage findByMessageId(String messageId);
}
