package com.song.demo.dto;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class resetPasswordDto implements Serializable {

    @NotEmpty(message = "密码不能为空")
    private String password;

    @NotEmpty(message = "新密码不能为空")
    private String newPassword;
}
