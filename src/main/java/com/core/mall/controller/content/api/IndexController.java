package com.core.mall.controller.content.api;

import com.core.mall.config.ApiTokenValidator;
import com.core.mall.model.params.IndexMainResp;
import com.core.mall.service.core.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @ApiTokenValidator
    @RequestMapping(value = "main", method = RequestMethod.GET)
    public IndexMainResp indexMain() {
        return indexService.indexMain();
    }
}
