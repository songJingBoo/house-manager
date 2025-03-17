package com.song.demo.dto.query;

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
 * 房源检索
 */
@Data
public class HouseQueryDto {

    private LayoutEn layout;

    private String city;

    private String community;

    private String name;

    private String phone;

    private String status;

    @Enumerated(EnumType.STRING)
    private IntentionEn intention;
}
