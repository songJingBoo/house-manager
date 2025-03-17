package com.song.demo.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.song.demo.common.BizException;
import com.song.demo.dto.LoginDto;
import com.song.demo.dto.UserDto;
import com.song.demo.service.impl.UserServiceImpl;
import com.song.demo.vo.LoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @ApiOperation("生成验证码")
    @GetMapping("/permit/captcha")
    public void getCaptche(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Cache-Control", "no-cache");
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(100, 41, 4, 4);
        captcha.write(response.getOutputStream());
        request.getSession().setAttribute("captchaCode", captcha.getCode());
    }

    @ApiOperation("登录")
    @PostMapping("/permit/login")
    public LoginVo login(HttpServletRequest request, @RequestBody @Valid LoginDto LoginDto) {
        String captchaCode = (String) request.getSession().getAttribute("captchaCode");
        if (!Objects.equals(captchaCode, LoginDto.getCaptcha())) {
            throw new BizException("验证码错误");
        }
        return userServiceImpl.login(LoginDto);
    }

    @ApiOperation("注册")
    @PostMapping("/permit/register")
    public boolean register(@RequestBody @Valid UserDto userDto) {
        return userServiceImpl.register(userDto);
    }

    @ApiOperation("登出")
    @GetMapping("/logout")
    public boolean logout() {
        return userServiceImpl.logout();
    }

    @ApiOperation("获取用户详情")
    @Secured("ROLE_abc")
    @GetMapping("/detail")
    public String detail() {
        throw new BizException("hahha");
//        return "detail";
    }
}
