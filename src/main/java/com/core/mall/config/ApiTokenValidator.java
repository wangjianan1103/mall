package com.core.mall.config;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiTokenValidator {

    UtcKey[] value() default {UtcKey.u, UtcKey.t, UtcKey.c};

    enum UtcKey {
        u, t, c;
    }
}
