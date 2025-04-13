package com.song.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.demo.dto.query.HouseQueryDto;
import com.song.demo.entity.HouseImagePo;
import com.song.demo.vo.HouseImageVo;
import com.song.demo.vo.HouseVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HouseImageMapper extends BaseMapper<HouseImagePo> {
    /**
     * 获取房屋图片
     * @param houseId
     * @return
     */
    @Select("select * from house_images where house_id = #{houseId}")
    List<HouseImageVo> queryImageList(@Param("houseId") String houseId);
}
