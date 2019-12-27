package com.core.mall.repository;

import com.core.mall.model.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUserGid(String userGid);

    UserAddress findByGid(String gid);

}