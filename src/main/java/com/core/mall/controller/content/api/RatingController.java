package com.core.mall.controller.content.api;

import com.core.mall.config.ApiTokenValidator;
import com.core.mall.model.params.RatingListResp;
import com.core.mall.service.core.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rating")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @ApiTokenValidator
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public RatingListResp ratingList() {
        return ratingService.ratingList();
    }
}
