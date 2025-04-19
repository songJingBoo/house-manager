package com.song.demo.common;

public enum ReturnCode {
    C200(200, "操作成功"),
    C400(400, "请求错误"),
    C401(401, "匿名用户访问"),
    C403(403, "无访问权限"),
    C405(405, "请求类型错误"),
    C500(500, "系统异常"),
    C666(666, "认证异常或已过期，请重新登录"),

    PWD_ERR(1001,"误用户名或密码错"),
    ACCESS_DENIED(1002,"客户端认证失败");

    private final int code;
    private final String message;

    ReturnCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据code 获取 message
     * @param code
     * @return
     */
    public static String getMessageByCode(Integer code) {
        for (ReturnCode c : ReturnCode.values()) {
            if (code.equals(c.code)) {
                return c.message;
            }
        }
        return null;
    }
}