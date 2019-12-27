package com.core.mall.controller.content.api;

import com.core.mall.model.params.ConfigProductResp;
import com.core.mall.model.params.ProductListResp;
import com.core.mall.service.core.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ProductListResp productList() {
        return productService.productList();
    }

    @RequestMapping(value = "/type/list", method = RequestMethod.GET)
    public ConfigProductResp productTypeList() {
        return productService.productTypeList();
    }

}
