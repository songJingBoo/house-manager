package com.song.demo.dto;

import com.song.demo.enums.HouseAuditStatusEn;
import com.song.demo.enums.HouseStatusEn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AuditDto {

    @NotNull(message = "房屋ID不能为空")
    private Long id;

    @NotNull(message = "状态不能为空")
    @Enumerated(EnumType.STRING)
    private HouseAuditStatusEn status;

    private String remarks;
}
