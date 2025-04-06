package com.song.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

/**
 * 用户登录实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不能为空")
    private String username;

    /**
     * 用户密码（加密）
     */
    @NotEmpty(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    @NotEmpty(message = "验证码不能为空")
    private String captcha;
}
