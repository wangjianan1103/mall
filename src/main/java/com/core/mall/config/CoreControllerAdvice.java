package com.core.mall.config;

import com.core.mall.enums.ErrorCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * controller 增强器
 *
 * 1. 异常统一处理
 * 2. 接口统一返回
 */
@RestControllerAdvice
public class CoreControllerAdvice implements ResponseBodyAdvice<Object> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 全局异常捕捉处理
     *
     * @param ex 系统异常
     * @return 统一返回内容数据
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseInfo errorHandler(Exception ex) {
        logger.error("exception, ex={}, url={}", ex.getMessage(), this.doURILog());
        ex.printStackTrace();
        return new ResponseInfo(ErrorCodeEnum.SYS_FAIL.getErrorCode(), ex.getMessage());
    }

    /**
     * 拦截捕捉自定义异常 CoreException.class
     * @param ex 自定义异常
     * @return 统一返回内容数据
     */
    @ResponseBody
    @ExceptionHandler(value = CoreException.class)
    public ResponseInfo coreErrorHandler(CoreException ex) {
        logger.error("core exception, ex={}, url={}", ex, this.doURILog());
        return new ResponseInfo(ex.getStatus(), ErrorCodeEnum.of(ex.getStatus()).getMessage());
    }

    private String doURILog() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取请求的request
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getRequestURL().toString();
        }
        return null;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 统一接口返回对象
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //sp1. 如果返回的body为null
        if(body == null){
            return new ResponseInfo(ErrorCodeEnum.SYS_SUCCESS);
        }

        //sp2. 文件上传下载，不需要改动，直接返回
        if (body instanceof Resource) {
            return body;
        }

        //sp3. 返回的是 String
        if (body instanceof String) {
            return body;
        }

        //sp4. 返回的是非字符串格式，实际上很多时候用都是是在应用程返回的对象居多
        if(body instanceof ResponseInfo){
            //情况5 如果已经封装成RestReturn,直接return
            return body;
        }

        //sp5. 非字符串非统一格式的返回，需要统一格式
        return new ResponseInfo(ErrorCodeEnum.SYS_SUCCESS, body);
    }
}
