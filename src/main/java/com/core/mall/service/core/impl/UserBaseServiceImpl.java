package com.core.mall.service.core.impl;

import com.core.mall.config.CoreException;
import com.core.mall.enums.ErrorCodeEnum;
import com.core.mall.model.entity.LogUserSubscribe;
import com.core.mall.model.entity.UserBase;
import com.core.mall.model.entity.UserWeChatInfo;
import com.core.mall.model.params.MineInfoResp;
import com.core.mall.model.params.RegisterUserMessage;
import com.core.mall.model.params.WeChatInfoParam;
import com.core.mall.model.params.WeChatInfoResp;
import com.core.mall.model.params.user.*;
import com.core.mall.model.wx.OAuthResponse;
import com.core.mall.repository.LogUserSubscribeRepository;
import com.core.mall.repository.UserBaseRepository;
import com.core.mall.repository.UserWeChatInfoRepository;
import com.core.mall.service.core.ApiTokenService;
import com.core.mall.service.core.UserBaseService;
import com.core.mall.service.wx.WxUserInfoService;
import com.core.mall.util.GlobalConstants;
import com.core.mall.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Objects;

@Service
public class UserBaseServiceImpl implements UserBaseService {
    private final static Logger logger = LoggerFactory.getLogger(UserBaseServiceImpl.class);

    @Autowired
    private UserWeChatInfoRepository userWeChatInfoRepository;

    @Autowired
    private LogUserSubscribeRepository logUserSubscribeRepository;

    @Autowired
    private UserBaseRepository userBaseRepository;

    @Autowired
    private WxUserInfoService wxUserInfoService;

    @Autowired
    private ApiTokenService apiTokenService;

    @Override
    public String registerWX(RegisterUserMessage message) {
        logger.info("registerUser() start, message={}", message);
        if (message == null || message.getOpenId() == null) {
            logger.info("registerUser() param is null, message={}", message);
            return null;
        }

        String openId = message.getOpenId();
        String unionId = message.getUnionId();
        int currentTimeStamp = Utility.getCurrentTimeStamp();
        UserWeChatInfo userWeChatInfo = userWeChatInfoRepository.findByOpenId(message.getOpenId());
        String userGid = null;
        if (userWeChatInfo == null) {

            String nickName = message.getNickName();
            userGid = Utility.generateUUID();
            UserWeChatInfo newInfo = new UserWeChatInfo();
            newInfo.setCreateTime(currentTimeStamp);
            newInfo.setUpdateTime(currentTimeStamp);
            newInfo.setUserGid(userGid);
            newInfo.setOpenId(openId);
            newInfo.setUnionId(unionId);
            newInfo.setNickName(nickName);
            newInfo.setCity(message.getCity());
            newInfo.setProvince(message.getProvince());
            newInfo.setCountry(message.getCountry());
            newInfo.setSex(message.getSex());
            newInfo.setHeadImgUrl(message.getHeadImgUrl());
            newInfo.setSubscribeStatus(GlobalConstants.SubscribeStatus.SUBSCRIBE_STATUS_FOLLOWED);
            newInfo.setSubscribeWay(Utility.converSubcribeWay(message.getSubscribeScene()));

            UserBase userBase = new UserBase();
            userBase.setGid(newInfo.getUserGid());
            userBase.setCreateTime(Utility.getCurrentTimeStamp());
            userBase.setUpdateTime(Utility.getCurrentTimeStamp());
            userBase.setNickname(newInfo.getNickName());
            userBase.setPortraitUrl(newInfo.getHeadImgUrl());

            userWeChatInfoRepository.save(newInfo);
            userBaseRepository.save(userBase);
        } else {
            userGid = userWeChatInfo.getUserGid();
            UserWeChatInfo updateInfo = new UserWeChatInfo();
            updateInfo.setId(userWeChatInfo.getId());
            updateInfo.setUpdateTime(currentTimeStamp);
            updateInfo.setNickName(message.getNickName());
            updateInfo.setCity(message.getCity());
            updateInfo.setProvince(message.getProvince());
            updateInfo.setCountry(message.getCountry());
            updateInfo.setSex(message.getSex());
            updateInfo.setHeadImgUrl(message.getHeadImgUrl());
            updateInfo.setSubscribeStatus(GlobalConstants.SubscribeStatus.SUBSCRIBE_STATUS_AGAIN_FOLLOWED);
            updateInfo.setSubscribeWay(Utility.converSubcribeWay(message.getSubscribeScene()));
            userWeChatInfoRepository.save(updateInfo);
        }

        LogUserSubscribe logUserSubscribe = new LogUserSubscribe();
        logUserSubscribe.setCreateTime(currentTimeStamp);
        logUserSubscribe.setUpdateTime(currentTimeStamp);
        logUserSubscribe.setOpenId(openId);
        logUserSubscribe.setUnionId(unionId);
        logUserSubscribe.setSubscribeType(GlobalConstants.LogSubscribeType.LOG_SUBSCRIBE_TYPE_FOLLOWED);
        logUserSubscribeRepository.save(logUserSubscribe);
        logger.info("registerUser() success end. userGig={}", userGid);
        return userGid;
    }

    @Override
    public void unRegisterWx(String openId) {
        logger.info("unRegisterUser() start, openId={}", openId);
        if (openId == null) {
            logger.error("unRegisterUser() param is null");
            return;
        }
        UserWeChatInfo userWeChatInfo = userWeChatInfoRepository.findByOpenId(openId);
        if (userWeChatInfo == null) {
            logger.error("unRegisterUser() param is null, openId={}", openId);
            return;
        }

        int currentTimeStamp = Utility.getCurrentTimeStamp();

        UserWeChatInfo updateInfo = new UserWeChatInfo();
        updateInfo.setId(userWeChatInfo.getId());
        updateInfo.setUpdateTime(currentTimeStamp);
        updateInfo.setSubscribeStatus(GlobalConstants.SubscribeStatus.SUBSCRIBE_STATUS_UN_FOLLOWED);
        userWeChatInfoRepository.save(updateInfo);

        LogUserSubscribe logUserSubscribe = new LogUserSubscribe();
        logUserSubscribe.setCreateTime(currentTimeStamp);
        logUserSubscribe.setUpdateTime(currentTimeStamp);
        logUserSubscribe.setOpenId(openId);
        logUserSubscribe.setUnionId(userWeChatInfo.getUnionId());
        logUserSubscribe.setSubscribeType(GlobalConstants.LogSubscribeType.LOG_SUBSCRIBE_TYPE_UN_FOLLOWED);
        logUserSubscribeRepository.save(logUserSubscribe);
        logger.info("unRegisterUser() success end. ");
    }

    @Override
    public WeChatInfoResp weChatInfo(@RequestBody WeChatInfoParam req) {
        logger.info("weChatInfo() start, req={}", req);
        String code = req.getCode();

        // 根据oauth2 code获取用户openId
        OAuthResponse oAuthResponse = wxUserInfoService.getOAuthByCode(code);
        if (oAuthResponse == null) {
            throw new CoreException(ErrorCodeEnum.SYS_FAIL);
        }
        String openID = oAuthResponse.getOpenid();
        if (openID == null) {
            logger.error("weChatInfo() openID is null, req={}", req);
            throw new CoreException(ErrorCodeEnum.SYS_FAIL);
        }
        UserWeChatInfo userWeChatInfo = userWeChatInfoRepository.findByOpenId(openID);
        if (userWeChatInfo != null) {
            return getUserBaseStatus(userWeChatInfo.getUserGid());
        }

        // 注册
        String userGid = wxUserInfoService.doSubscribe(openID);
        return getUserBaseStatus(userGid);
    }

    private WeChatInfoResp getUserBaseStatus(String userGid) {
        logger.error("getUserBaseStatus() start, userGid={}", userGid);
        WeChatInfoResp response = new WeChatInfoResp();

        UserBase userBase = userBaseRepository.findByGid(userGid);
        if (userBase == null) {
            return response;
        }

        String token = apiTokenService.getUtcToken(userBase.getId());
        if (token == null) {
            token = apiTokenService.generateToken(userBase.getId());
        }

        response.setUid(userBase.getId());
        response.setUserGid(userGid);
        response.setToken(token);
        return response;
    }

    @Override
    public MineInfoResp getMineInfo(String userGid) {
        logger.error("getMineInfo() start, userGid={}", userGid);
        if (userGid == null) {
            logger.error("getMineInfo() userGid is null");
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }
        MineInfoResp response = null;

        UserBase userBase = userBaseRepository.findByGid(userGid);
        if (userBase != null) {
            response = new MineInfoResp();
            response.setNickName(userBase.getNickname());
            response.setPortraitUrl(userBase.getPortraitUrl());
            response.setUserGid(userBase.getGid());
        }
        return response;
    }

    @Override
    public LoginResp login(LoginParam param) {
        String mobile = param.getMobile();
        String password = param.getPassword();
        if (mobile == null || password == null) {
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        UserBase userBase = userBaseRepository.findByMobile(mobile);
        if (userBase == null) {
            throw new CoreException(ErrorCodeEnum.USER_NOT_EXIST_ERROR);
        }

        if (!Objects.equals(Utility.getMd5(password), String.valueOf(userBase.getLoginPwd()))) {
            throw new CoreException(ErrorCodeEnum.USER_LOGIN_PWD_ERROR);
        }

        return new LoginResp(loginResponse(userBase.getGid()));
    }

    @Override
    public RegisterResp register(RegisterParam param) {
        String mobile = param.getMobile();
        String password = param.getPassword();
        if (mobile == null || password == null) {
            throw new CoreException(ErrorCodeEnum.SYS_PARAMETER_ERROR);
        }

        UserBase userBase = userBaseRepository.findByMobile(mobile);
        if (userBase != null) {
            throw new CoreException(ErrorCodeEnum.USER_IS_EXIST_ERROR);
        }

        UserBase newUserBase = new UserBase();
        newUserBase.setGid(Utility.generateUUID());
        newUserBase.setCreateTime(Utility.getCurrentTimeStamp());
        newUserBase.setUpdateTime(Utility.getCurrentTimeStamp());
        newUserBase.setMobile(mobile);
        newUserBase.setLoginPwd(Utility.getMd5(password));

        userBaseRepository.saveAndFlush(newUserBase);
        return new RegisterResp(loginResponse(newUserBase.getGid()));
    }

    private BaseLogin loginResponse(String userGid) {
        BaseLogin baseLogin = new BaseLogin();
        UserBase userBase = userBaseRepository.findByGid(userGid);
        if (userBase == null) {
            return baseLogin;
        }

        String token = apiTokenService.getUtcToken(userBase.getId());
        if (token == null) {
            token = apiTokenService.generateToken(userBase.getId());
        }

        baseLogin.setUid(userBase.getId());
        baseLogin.setUserGid(userGid);
        baseLogin.setT(token);
        return baseLogin;
    }

}
