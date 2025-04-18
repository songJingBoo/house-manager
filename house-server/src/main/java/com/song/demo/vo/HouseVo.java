package com.song.demo.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.song.demo.entity.HouseImagePo;
import com.song.demo.enums.HouseAuditStatusEn;
import com.song.demo.enums.HouseStatusEn;
import com.song.demo.enums.IntentionEn;
import com.song.demo.enums.LayoutEn;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HouseVo implements Serializable {

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
    private BigDecimal finalPrice;

    private String name;

    private String phone;

    @Enumerated(EnumType.STRING)
    private IntentionEn intention;

    @Enumerated(EnumType.STRING)
    private HouseStatusEn status;

    @Enumerated(EnumType.STRING)
    private HouseAuditStatusEn auditStatus;

    private String agent;

    private String agentName;

    private String agentPhone;

    private Integer isLiked;

    private List<HouseImageVo> images;

    private String imageCover;

    /**
     * 预约相关
     */
    private Long appointId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
    private LocalDateTime appointStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
    private LocalDateTime appointEndTime;

    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String updater;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
