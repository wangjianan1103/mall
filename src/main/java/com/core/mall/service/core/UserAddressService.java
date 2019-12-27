package com.core.mall.service.core;


import com.core.mall.model.params.*;

public interface UserAddressService {

    AddressInfoResp addressInfo(AddressInfoParam param);

    String addressAdd(AddressAddParam param);

    String addressDelete(AddressDeleteParam param);

    AddressResp getAddress(AddressParam param);
}
