package com.song.demo.config;

import com.alibaba.fastjson.JSON;
import com.song.demo.common.ResultData;
import com.song.demo.common.ReturnCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足处理类
 * 访问的资源权限不足时
 * 将调用此方法发送403响应以及错误信息
 */
public class RestfulAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                  HttpServletResponse response,
                  AccessDeniedException e) throws IOException, ServletException {

        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().println(JSON.toJSON(ResultData.fail(ReturnCode.C403.getCode(), null)));
        response.getWriter().flush();
    }
}
