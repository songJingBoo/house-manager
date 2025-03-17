package com.song.demo.common;

/**
 * 自定义业务异常
 */
public class BizException extends RuntimeException {
    private String message;

    public BizException(String message) {
        super(message);
        this.message = message;
    }
}
