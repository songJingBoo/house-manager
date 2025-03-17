package com.song.demo.common;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ResultData<T> {
    private int status;
    private T data;
    private String message;
    private long timestamp;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.C200.getCode());
        resultData.setMessage(ReturnCode.C200.getMessage());
        resultData.setData(data);
        return resultData;
    }

    /**
     * 失败 - 自定义code[, message]
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T>ResultData<T> fail(int code, @Nullable String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(code);
        if (message != null) {
            resultData.setMessage(message);
        } else {
            String defaultMessage = ReturnCode.getMessageByCode(code);
            resultData.setMessage(defaultMessage != null ? defaultMessage : "");
        }
        return resultData;
    }

    /**
     * 失败 - 自定义message（默认500）
     * @param message
     * @param <T>
     * @return
     */
    public static <T>ResultData<T> fail(String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.C500.getCode());
        resultData.setMessage(message);
        return resultData;
    }
}
