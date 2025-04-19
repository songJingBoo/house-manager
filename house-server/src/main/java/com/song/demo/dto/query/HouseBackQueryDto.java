package com.song.demo.dto.query;

import com.song.demo.enums.IntentionEn;
import com.song.demo.enums.LayoutEn;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * 房源检索
 */
@Data
public class HouseBackQueryDto {

    private LayoutEn layout;

    private String city;

    private String community;

    private String name;

    private String phone;

    private String status;

    private String userId;

    @Enumerated(EnumType.STRING)
    private IntentionEn intention;
}
