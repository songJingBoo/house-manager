package com.song.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.demo.dto.HouseFilterDto;
import com.song.demo.entity.HouseFilterPo;
import com.song.demo.entity.HouseImagePo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface HouseFilterMapper extends BaseMapper<HouseFilterPo> {

    /**
     * 保存过滤条件配置
     * @param dto
     * @return
     */
    @Update("update house_filter set status = ${dto.status}, config = #{dto.config} where id = ${dto.id}")
    int saveFilterConfig(@Param("dto") HouseFilterDto dto);
}
