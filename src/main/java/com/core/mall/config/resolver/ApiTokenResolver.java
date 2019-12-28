package com.core.mall.config.resolver;

import com.core.mall.config.CoreException;
import com.core.mall.enums.ErrorCodeEnum;
import com.core.mall.service.core.ApiTokenService;
import com.core.mall.util.Utility;
import com.google.gson.Gson;
import javassist.bytecode.SignatureAttribute;
import org.apache.http.protocol.HTTP;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * API Token resolver
 */
@Order(Integer.MIN_VALUE + 10)
@Aspect
@Component
public class ApiTokenResolver {
    private static final Logger logger = LoggerFactory.getLogger(ApiTokenResolver.class);

    @Autowired
    private ApiTokenService apiTokenService;

    @Pointcut(value = "@annotation(com.core.mall.config.ApiTokenValidator)")
    private void doApiUtcPct() {
    }

    /**
     * check api UTC
     *
     */
    @Around("doApiUtcPct()")
    private Object checkApiUtcToken(ProceedingJoinPoint jp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        assert response != null;
        response.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
        try {
            // u
            final String u = request.getHeader("Authorization");
            if (Utility.isBlank(u)) {
                logger.error("checkApiUtcToken: uid invalid, u={}", u);
                setTokenInvalidResp(response);
                return null;
            }
            final int userId = Integer.parseInt(u);
            // t
            final String t = request.getHeader("uid");
            if (Utility.isBlank(t)) {
                logger.error("checkApiUtcToken: token is blank.");
                setTokenInvalidResp(response);
                return null;
            }

            logger.debug("checkApiUtcToken: arg uri={}, u={}, t={}", request.getRequestURI(), u, t);

            // s1: check token
            if (!apiTokenService.checkApiUtc(userId, t)) {
                logger.error("checkApiUtcToken: check API UTC token fail, uid={},token={}", u, t);
                setTokenInvalidResp(response);
                return null;
            }

            return jp.proceed();
        } catch (Exception e) {
            throw e;
        }
    }

    private void setTokenInvalidResp(HttpServletResponse response) throws IOException {
        throw new CoreException(ErrorCodeEnum.SYS_TOKEN_INVALID);
    }

}
