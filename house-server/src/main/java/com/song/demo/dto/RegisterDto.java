package com.song.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 用户注册实体类
 */
@Data
public class RegisterDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    private String password;

    /**
     * 邮箱
     */
    @NotEmpty(message = "邮箱不能为空")
    private String email;

    /**
     * 电话
     */
    @NotEmpty(message = "电话不能为空")
    private String phone;
}
