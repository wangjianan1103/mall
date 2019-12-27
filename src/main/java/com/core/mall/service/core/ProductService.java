package com.core.mall.service.core;

import com.core.mall.model.params.ConfigProductResp;
import com.core.mall.model.params.ProductListResp;

public interface ProductService {

    ProductListResp productList();

    ConfigProductResp productTypeList();
}
