package com.song.demo.dto;

import com.song.demo.enums.IntentionEn;
import com.song.demo.enums.LayoutEn;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 房源 操作
 */
@Data
public class HouseFilterDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "过滤项id不能为空")
    private Integer id;

    @NotNull(message = "过滤项状态不能为空")
    private Integer status;

    @NotEmpty(message = "过滤项配置不能为空")
    private String config;
}
