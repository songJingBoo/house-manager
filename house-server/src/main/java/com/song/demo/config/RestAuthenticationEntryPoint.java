package com.song.demo.config;

import com.alibaba.fastjson.JSON;
import com.song.demo.common.ResultData;
import com.song.demo.common.ReturnCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 用户未登录异常处理类
 * 需要认证，但无Token 或 Token错误 或 Token过期时
 * 将调用此方法发送777响应以及错误信息
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().println(JSON.toJSON(ResultData.fail(ReturnCode.C666.getCode(), null)));
        response.getWriter().flush();
    }
}
