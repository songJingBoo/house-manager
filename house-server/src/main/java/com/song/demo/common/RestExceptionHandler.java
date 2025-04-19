package com.song.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice // RestController的增强类，可用于实现全局异常处理器
public class RestExceptionHandler {

    /**
     * 默认全局异常处理
     * @param e
     * @return ResultData
     */
//    @ExceptionHandler(Exception.class) // 统一处理某一类异常，从而减少代码重复率和复杂度，比如要获取自定义异常可以@ExceptionHandler(BusinessException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 指定客户端收到的http状态码
//    public ResultData<String> exception(Exception e) {
//        log.error("Error occurred: ", e);
//        return ResultData.fail(ReturnCode.C500.getCode(), e.getMessage());
//    }

    /**
     * 处理自定义业务异常
     * @param e
     * @return ResultData
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK) // 指定客户端收到的http状态码
    public ResultData<String> bizException(Exception e) {
        log.error("Biz Error: ", e);
        return ResultData.fail(ReturnCode.C400.getCode(), e.getMessage());
    }

    /**
     * 处理@Valid实体类时，当中的@NotNull、@NotBlank...类型错误
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultData exceptionHandler(MethodArgumentNotValidException e) {
        log.error("Params Error: ", e);
        return ResultData.fail(ReturnCode.C400.getCode(), e.getBindingResult().getFieldError().getDefaultMessage());
    }
}
