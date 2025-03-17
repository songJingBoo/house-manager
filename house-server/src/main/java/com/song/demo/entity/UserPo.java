package com.song.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.song.demo.enums.RoleEn;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class UserPo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String userId;

    private String username;

    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private RoleEn role;

    /**
     * 是否系统预置（0、1）
     */
    private Integer isDefault;

    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String updater;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
