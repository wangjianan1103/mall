package com.core.mall.controller.content.api;

import com.core.mall.config.ApiTokenValidator;
import com.core.mall.model.params.MineInfoParam;
import com.core.mall.model.params.MineInfoResp;
import com.core.mall.model.params.WeChatInfoParam;
import com.core.mall.model.params.WeChatInfoResp;
import com.core.mall.model.params.user.LoginParam;
import com.core.mall.model.params.user.LoginResp;
import com.core.mall.model.params.user.RegisterParam;
import com.core.mall.model.params.user.RegisterResp;
import com.core.mall.service.core.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserBaseController {

    @Autowired
    private UserBaseService userBaseService;

    @RequestMapping(value = "/weChatStatus", method = RequestMethod.POST)
    public WeChatInfoResp weChatStatus(@RequestBody WeChatInfoParam param) {
        return userBaseService.weChatInfo(param);
    }

    @ApiTokenValidator
    @RequestMapping(value = "/mineInfo", method = RequestMethod.POST)
    public MineInfoResp MineInfo(@RequestBody MineInfoParam param) {
        return userBaseService.getMineInfo(param.getUserGid());
    }

    /**
     * 登录
     *
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResp Login(@RequestBody LoginParam param) {
        return userBaseService.login(param);
    }

    /**
     * 注册
     *
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RegisterResp Register(@RequestBody RegisterParam param) {
        return userBaseService.register(param);
    }
}
