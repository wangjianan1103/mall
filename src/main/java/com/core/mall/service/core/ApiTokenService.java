package com.core.mall.service.core;

public interface ApiTokenService {

    boolean checkApiUtc(long uid, String t);

    String getUtcToken(long uid);

    String generateToken(long uid);

}
