package com.core.mall.config;

import com.core.mall.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CoreException extends RuntimeException {
    private int status;
    private String message;

    public CoreException(int status) {
        this.status = status;
    }

    public CoreException(ErrorCodeEnum errorCodeEnum) {
        this.status = errorCodeEnum.getErrorCode();
    }

    public CoreException(int status, String message) {
        this.status = status;
        this.message = message;
    }
}



