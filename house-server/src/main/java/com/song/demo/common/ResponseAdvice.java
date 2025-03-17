package com.song.demo.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 拦截Controller方法的返回值，统一处理返回值/响应体，一般用来统一返回格式，加解密，签名等等
 */

// 该注解是@RestController的增强，可实现以下三个功能：
// .全局异常处理
// .全局数据绑定
// .全局数据预处理
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object res, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // Controller返回的String，SpringBoot是直接返回，故我们需要手动转换成json
        if (res instanceof String) {
            // springboot返回string时默认text/plian格式，由于我们会用ResultData封装，所以此处需要设置为json
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return objectMapper.writeValueAsString(ResultData.success(res));
        }
        // 全局异常已经被封装成了标准格式，直接返回即可
        if (res instanceof ResultData) {
            return res;
        }
        return ResultData.success(res);
    }
}
