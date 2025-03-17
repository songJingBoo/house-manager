package com.song.demo.dto;

import com.song.demo.enums.HouseStatusEn;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class AuditDto {

    @NotNull(message = "房屋ID不能为空")
    private long id;

    @NotNull(message = "状态不能为空")
    @Enumerated(EnumType.STRING)
    private HouseStatusEn status;

    private String remarks;
}
