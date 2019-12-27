package com.core.mall.service.core.impl;

import com.core.mall.config.cache.LocalCache;
import com.core.mall.service.core.ApiTokenService;
import com.core.mall.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApiTokenServiceImpl implements ApiTokenService {
    private final static Logger logger = LoggerFactory.getLogger(ApiTokenServiceImpl.class);

    private final static int ExpirePeriod = 60 * 60;

    @Override
    public boolean checkApiUtc(long uid, String t) {
        String utcInfo = LocalCache.get(getTokenCacheKey(uid));
        if (Utility.isBlank(utcInfo)) {
            return false;
        }

        return t.equals(utcInfo);
    }

    @Override
    public String getUtcToken(long uid) {
        return LocalCache.get(getTokenCacheKey(uid));
    }

    @Override
    public String generateToken(long uid) {
        final String newToken = Utility.generateUUID();
        this.setUtcInfo(uid, newToken);
        logger.debug("generateToken: new user uid={}, token={}", uid, newToken);
        return newToken;
    }

    private void setUtcInfo(final long uid, final String newToken) {
        String tokenKey = getTokenCacheKey(uid);
        LocalCache.put(tokenKey, newToken, ExpirePeriod);
    }

    private static String getTokenCacheKey(long uid) {
        return Utility.getCashKey("user_token_") + uid;
    }
}
