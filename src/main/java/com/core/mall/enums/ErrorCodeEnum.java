package com.core.mall.enums;


/**
 * 系统异常码枚举
 *
 * 0 ~ 100 系统异常
 *
 * 1000 登录 ...
 */
public enum ErrorCodeEnum {
    SYS_SUCCESS(0, "成功"),
    SYS_UP_REQ_ISNULL(1, "请求不能为空！"),
    SYS_PARAMETER_ERROR(2, "请求参数不正确！"),
    SYS_FAIL(10, "系统繁忙，请稍后重试！"),
    SYS_CONTROLLER_FAIL(11, "数据异常，请稍后重试！"),
    SYS_TOKEN_INVALID(401, "token失效！"),

    /**
     * 1000 ~ 1999 用户登录、注册异常码
     */
    USER_NOT_EXIST_ERROR(1000, "用户不存在！"),
    USER_IS_EXIST_ERROR(1001, "用户已注册！"),
    USER_LOGIN_PWD_ERROR(1002, "用户或密码异常！"),

    SYS_UN_KNOWN_FAIL(11, "未知异常");

    private int errorCode;
    private String message;

    ErrorCodeEnum(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorCodeEnum of(final int type) {
        for (ErrorCodeEnum em : ErrorCodeEnum.values()) {
            if (type == em.getErrorCode()) {
                return em;
            }
        }
        return SYS_FAIL;
    }
}
