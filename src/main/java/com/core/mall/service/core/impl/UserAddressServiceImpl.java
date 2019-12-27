package com.core.mall.service.core.impl;

import com.core.mall.config.CoreException;
import com.core.mall.enums.ErrorCodeEnum;
import com.core.mall.model.entity.UserAddress;
import com.core.mall.model.params.*;
import com.core.mall.repository.UserAddressRepository;
import com.core.mall.service.core.UserAddressService;
import com.core.mall.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {
    private final static Logger logger = LoggerFactory.getLogger(UserAddressServiceImpl.class);

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Override
    public AddressInfoResp addressInfo(AddressInfoParam req) {
        logger.info("addressInfo() request={}", req);
        if (req == null || req.getUserGid() == null) {
            logger.error("addressInfo() request is null");
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR.getErrorCode());
        }

        AddressInfoResp response = new AddressInfoResp();

        String userGid = req.getUserGid();
        List<UserAddress> list = userAddressRepository.findByUserGid(userGid);

        List<AddressInfoResp.AddressInfo> result = new ArrayList<>();
        for (UserAddress userAddress : list) {
            AddressInfoResp.AddressInfo info = new AddressInfoResp.AddressInfo();
            info.setName(userAddress.getName());
            info.setSex(userAddress.getSex());
            info.setGid(userAddress.getGid());
            info.setAddress(userAddress.getAddress());
            info.setTag(userAddress.getTag());
            info.setMobile(userAddress.getMobile());
            result.add(info);
        }
        response.setInfoList(result);
        logger.info("addressInfo(), end response={}", response);
        return response;
    }

    @Override
    public String addressAdd(AddressAddParam req) {
        logger.info("addressAdd() request={}", req);
        if (req == null || req.getUserGid() == null) {
            logger.error("addressAdd() request is null");
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR.getErrorCode());
        }
        String address = req.getAddress();
        String userGid = req.getUserGid();
        String mobile = req.getMobile();
        String name = req.getName();
        String gid = req.getGid();
        int sex = req.getSex();
        int tag = req.getTag();

        if (Utility.isBlank(address) || Utility.isBlank(mobile) || Utility.isBlank(name)) {
            logger.error("addressAdd() params is null, request={}", req);
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR.getErrorCode());
        }

        UserAddress userAddress = new UserAddress();
        userAddress.setAddress(address);
        userAddress.setUpdateTime(Utility.getCurrentTimeStamp());
        userAddress.setSex(sex);
        userAddress.setTag(tag);
        userAddress.setUserGid(userGid);
        userAddress.setMobile(mobile);
        userAddress.setName(name);
        if (Utility.isNotBlank(gid)) {
            UserAddress userAddressDb = userAddressRepository.findByGid(gid);
            if (userAddressDb == null) {
                throw new CoreException(ErrorCodeEnum.SYS_FAIL.getErrorCode());
            }
            userAddress.setId(userAddressDb.getId());
            userAddressRepository.saveAndFlush(userAddress);
        } else {
            userAddress.setCreateTime(Utility.getCurrentTimeStamp());
            userAddress.setGid(Utility.generateUUID());
            userAddressRepository.saveAndFlush(userAddress);
        }

        logger.info("addressAdd() add address success");
        return "SUCCESS";
    }

    @Override
    public String addressDelete(AddressDeleteParam req) {
        logger.info("addressDelete() request={}", req);
        if (req == null || req.getUserGid() == null) {
            logger.error("addressDelete() request is null");
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR.getErrorCode());
        }

        if (req.getGid() == null) {
            logger.error("addressDelete() request is null");
            throw new CoreException(ErrorCodeEnum.SYS_FAIL.getErrorCode());
        }

        String gid = req.getGid();
        UserAddress userAddressDb = userAddressRepository.findByGid(gid);
        if (userAddressDb == null) {
            throw new CoreException(ErrorCodeEnum.SYS_FAIL.getErrorCode());
        }

        UserAddress userAddress = new UserAddress();
        userAddress.setUpdateTime(Utility.getCurrentTimeStamp());
        userAddress.setGid(gid);
        userAddress.setIsValid(Boolean.FALSE);
        userAddress.setId(userAddressDb.getId());
        userAddressRepository.saveAndFlush(userAddress);
        logger.info("addressDelete() delete address success");
        return "SUCCESS";
    }

    @Override
    public AddressResp getAddress(AddressParam req) {
        logger.info("getAddress() request={}", req);
        if (req == null || req.getUserGid() == null) {
            logger.error("getAddress() request is null");
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR.getErrorCode());
        }

        UserAddress userAddress = this.selectByGidOrDefault(req.getUserGid(), req.getAddressGid());
        if (userAddress == null || !userAddress.getIsValid()) {
            logger.error("getAddress() request is null");
            throw new CoreException(ErrorCodeEnum.SYS_FAIL.getErrorCode());
        }

        AddressResp response = new AddressResp();
        response.setName(userAddress.getName());
        response.setSex(userAddress.getSex());
        response.setGid(userAddress.getGid());
        response.setAddress(userAddress.getAddress());
        response.setTag(userAddress.getTag());
        response.setMobile(userAddress.getMobile());
        return response;
    }


    private UserAddress selectByGidOrDefault(String userGid, String addressGid) {
        List<UserAddress> addressList = userAddressRepository.findByUserGid(userGid);
        if (!CollectionUtils.isEmpty(addressList)) {
            return addressList.get(0);
        }
        return null;
    }
}
