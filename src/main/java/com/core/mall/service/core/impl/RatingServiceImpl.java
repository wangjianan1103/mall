package com.core.mall.service.core.impl;

import com.core.mall.model.entity.RecordProduct;
import com.core.mall.model.entity.RecordProductOrder;
import com.core.mall.model.entity.RecordRating;
import com.core.mall.model.entity.UserWeChatInfo;
import com.core.mall.model.params.RatingListResp;
import com.core.mall.repository.RecordProductOrderRepository;
import com.core.mall.repository.RecordProductRepository;
import com.core.mall.repository.RecordRatingRepository;
import com.core.mall.repository.UserWeChatInfoRepository;
import com.core.mall.service.core.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RecordRatingRepository recordRatingRepository;

    @Autowired
    private UserWeChatInfoRepository userWeChatInfoRepository;

    @Autowired
    private RecordProductOrderRepository recordProductOrderRepository;

    @Autowired
    private RecordProductRepository recordProductRepository;

    @Override
    public RatingListResp ratingList() {
        RatingListResp response = new RatingListResp();
        List<RecordRating> list = recordRatingRepository.findAll();
        if (list == null) {
            return response;
        }

        List<RatingListResp.Rating> result = new ArrayList<>();
        for (RecordRating recordRating : list) {
            RatingListResp.Rating rating = new RatingListResp.Rating();

            rating.setDeliveryTime(recordRating.getDeliveryTime());
            rating.setRateType(recordRating.getRateType());
            rating.setRatingDesc(recordRating.getRateDesc());
            rating.setScore(recordRating.getScore());
            rating.setRatingTime(recordRating.getCreateTime());

            String userGid = recordRating.getUserGid();
            String orderGid = recordRating.getOrderGid();
            UserWeChatInfo userWeChatInfo = userWeChatInfoRepository.findByUserGid(userGid);
            if (userWeChatInfo != null) {
                rating.setName(userWeChatInfo.getNickName());
                rating.setUserImg(userWeChatInfo.getHeadImgUrl());
            }

            List<RecordProductOrder> orderList = recordProductOrderRepository.findByOrderGid(orderGid);
            if (orderList != null) {
                List<String> recommend = new ArrayList<>();
                for (RecordProductOrder order : orderList) {
                    RecordProduct recordProduct = recordProductRepository.findByProductId(order.getProductGid());
                    recommend.add(recordProduct.getName());
                }
                rating.setRecommend(recommend);
            }
            result.add(rating);
        }

        response.setRatingList(result);
        return response;
    }
}
