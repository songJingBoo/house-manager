package com.song.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.demo.dto.query.HouseBackQueryDto;
import com.song.demo.dto.query.HouseQueryDto;
import com.song.demo.entity.HousePo;
import com.song.demo.vo.HouseVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseMapper extends BaseMapper<HousePo> {

    /**
     * 获取房源列表
     * @param query
     * @return
     */
    List<HouseVo> getHouse(@Param("query") HouseBackQueryDto query);


    /**
     * 获取房源列表
     * @param query
     * @return
     */
    List<HouseVo> queryHouse(@Param("query") HouseQueryDto query);

    /**
     * 获取房源详情
     * @param id
     * @return
     */
    HouseVo getHouseById(@Param("id") String id);

}
