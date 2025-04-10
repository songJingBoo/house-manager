package com.song.demo.dto.query;

import com.song.demo.dto.HouseFilterDto;
import com.song.demo.enums.IntentionEn;
import com.song.demo.enums.LayoutEn;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

/**
 * 房源检索
 */
@Data
public class HouseQueryDto {

    private String keyword;

    private String sortType;

    private String userId;

    private List<HouseFilterDto> filter;
}
