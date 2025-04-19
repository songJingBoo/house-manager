package com.song.demo.dto.query;

import com.song.demo.dto.HouseFilterConfigDto;
import com.song.demo.dto.HouseFilterDto;
import lombok.Data;

import java.util.List;

/**
 * 房源检索
 */
@Data
public class HouseQueryDto {

    private String keyword;

    private String sortType;

    private String sortDir;

    private String userId;

    private Boolean showFinish;

    private List<HouseFilterDto> filter;
}
