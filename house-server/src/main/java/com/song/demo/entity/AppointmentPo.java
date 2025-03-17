package com.song.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.song.demo.enums.AppointEn;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("appointment")
public class AppointmentPo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String userId;

    private String houseId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private AppointEn status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;
}
