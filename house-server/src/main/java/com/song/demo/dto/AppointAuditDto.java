package com.song.demo.dto;

import com.song.demo.enums.AppointEn;
import com.song.demo.enums.HouseAuditStatusEn;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
public class AppointAuditDto {

    @NotNull(message = "预约ID不能为空")
    private Long id;

    @NotNull(message = "状态不能为空")
    @Enumerated(EnumType.STRING)
    private AppointEn status;

    private String remarks;
}
