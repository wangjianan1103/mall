package com.core.mall.service.core.impl;

import com.core.mall.config.CoreException;
import com.core.mall.enums.ConfigGlobalEnum;
import com.core.mall.enums.ErrorCodeEnum;
import com.core.mall.model.entity.ConfigProduct;
import com.core.mall.model.entity.RecordProduct;
import com.core.mall.model.entity.RecordProductSpec;
import com.core.mall.model.entity.RecordRating;
import com.core.mall.model.params.ConfigProductResp;
import com.core.mall.model.params.ProductListResp;
import com.core.mall.model.params.ProductResponse;
import com.core.mall.model.params.ProductSpecResp;
import com.core.mall.repository.*;
import com.core.mall.service.core.ProductService;
import com.core.mall.util.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ConfigProductRepository configProductRepository;

    @Autowired
    private RecordProductRepository recordProductRepository;

    @Autowired
    private RecordRatingRepository recordRatingRepository;

    @Autowired
    private ConfigGlobalRepository configGlobalRepository;

    @Autowired
    private RecordProductSpecRepository recordProductSpecRepository;

    @Override
    public ProductListResp productList() {

        List<ConfigProduct> configProductList = configProductRepository.findAll();
        if (configProductList == null || configProductList.size() == 0) {
            logger.error("productList() config product is null");
            return null;
        }

        List<ProductListResp.ProductTypeResponse> resultList = new ArrayList<>();
        for (ConfigProduct product : configProductList) {
            ProductListResp.ProductTypeResponse response = new ProductListResp.ProductTypeResponse();

            int productTypeId = product.getProductTypeId();
            List<RecordProduct> list = recordProductRepository.findByProductTypeId(productTypeId);
            List<RecordRating> ratingList = recordRatingRepository.findAll();
            List<ProductResponse> productList = dealProduct(list, ratingList);

            response.setName(product.getName());
            response.setType(product.getType());
            response.setFoods(productList);
            resultList.add(response);
        }

        // init config
        String deliveryPrice = configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_GLOBAL_KEY_DELIVERY_PRICE.getValue());
        String sumDeliveryPrice = configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_GLOBAL_KEY_SUM_DELIVERY_PRICE.getValue());
        String minPrice = configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_GLOBAL_KEY_MIN_PRICE.getValue());
        if (deliveryPrice == null || minPrice == null || sumDeliveryPrice == null) {
            logger.error("productList() deliveryPrice or minPrice is null");
            throw new CoreException(ErrorCodeEnum.SYS_FAIL);
        }

        ProductListResp resultResponse = new ProductListResp();
        resultResponse.setResponse(resultList);
        resultResponse.setDeliveryPrice(new BigDecimal(deliveryPrice));
        resultResponse.setSumDeliveryPrice(new BigDecimal(sumDeliveryPrice));
        resultResponse.setMinPrice(new BigDecimal(minPrice));
        logger.info("productList() resultResponse={}");
        return resultResponse;
    }

    @Override
    public ConfigProductResp productTypeList() {
        ConfigProductResp response = new ConfigProductResp();

        List<ConfigProduct> configProductList = configProductRepository.findAll();
        if (configProductList == null || configProductList.size() == 0) {
            logger.error("productList() config product is null");
            return null;
        }

        List<ConfigProductResp.ProductType> list = new ArrayList<>();
        for (ConfigProduct product : configProductList) {
            ConfigProductResp.ProductType productType = new ConfigProductResp.ProductType();
            productType.setName(product.getName());
            productType.setIco(product.getIco());
            productType.setTypeId(product.getProductTypeId());
            productType.setValid(product.getIsValid());
            productType.setSequence(product.getSequence());
            list.add(productType);
        }
        response.setList(list);
        return response;
    }

    private List<ProductResponse> dealProduct(List<RecordProduct> productList, List<RecordRating> ratingList) {
        List<ProductResponse> productResponseList = new ArrayList<>();
        if (productList == null || productList.size() == 0) {
            return productResponseList;
        }

        for (RecordProduct recordProduct : productList) {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setGid(recordProduct.getProductId());
            productResponse.setName(recordProduct.getName());
            productResponse.setPrice(recordProduct.getPrice());
            productResponse.setOldPrice(recordProduct.getOldPrice());
            productResponse.setDescription(recordProduct.getDescription());
            productResponse.setInfo(recordProduct.getInfo());
            productResponse.setSellCount(222);
            productResponse.setRating(99);
            productResponse.setIcon(recordProduct.getIcon());
            productResponse.setImage(recordProduct.getImage());
            productResponse.setSpecType(recordProduct.getSpecType());

            if (GlobalConstants.RecordProductSpec.RECORD_PRODUCT_SPEC_SUM == recordProduct.getSpecType()) {
                // 多规格
                List<RecordProductSpec> specList = recordProductSpecRepository.findByProductId(recordProduct.getProductId());
                if (specList == null || specList.size() <= 0) {
                    throw new CoreException(ErrorCodeEnum.SYS_FAIL);
                }
                List<ProductSpecResp> specResponseList = new ArrayList<>();
                for (RecordProductSpec spec : specList) {
                    ProductSpecResp specResponse = new ProductSpecResp();
                    specResponse.setId(spec.getId());
                    specResponse.setName(spec.getName());
                    specResponse.setPrice(spec.getPrice());
                    specResponseList.add(specResponse);
                }

                productResponse.setSpecList(specResponseList);
            }

            productResponseList.add(productResponse);
        }

        return productResponseList;
    }
}
