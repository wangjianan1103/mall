package com.core.mall.controller.content.api;

import com.core.mall.model.params.*;
import com.core.mall.service.core.UserAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAddressController {
    private final static Logger logger = LoggerFactory.getLogger(UserAddressController.class);

    @Autowired
    private UserAddressService userAddressService;

    @RequestMapping(value = "/addressInfo", method = RequestMethod.POST)
    public AddressInfoResp addressInfo(@RequestBody AddressInfoParam req) {
        return userAddressService.addressInfo(req);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(@RequestBody AddressAddParam param) {
        return userAddressService.addressAdd(param);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestBody AddressDeleteParam param) {
        return userAddressService.addressDelete(param);
    }

    @RequestMapping(value = "/getAddress", method = RequestMethod.POST)
    public AddressResp getAddress(@RequestBody AddressParam param) {
        return userAddressService.getAddress(param);
    }
}
