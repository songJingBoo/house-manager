package com.song.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.song.demo.enums.HouseStatusEn;
import com.song.demo.enums.IntentionEn;
import com.song.demo.enums.LayoutEn;
import com.song.demo.enums.RoleEn;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Data
@TableName("houses")
public class HousePo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String userId;

    private String houseId;

    private String title;

    @Enumerated(EnumType.STRING)
    private LayoutEn layout;

    private BigDecimal area;

    private Integer floor;

    private String city;

    private String community;

    private String address;

    private BigDecimal expectPrice;

    private String name;

    private String phone;

    @Enumerated(EnumType.STRING)
    private IntentionEn intention;

    @Enumerated(EnumType.STRING)
    private HouseStatusEn status;

    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String updater;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
