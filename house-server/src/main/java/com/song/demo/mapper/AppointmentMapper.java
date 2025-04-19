package com.song.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.demo.dto.query.AppointmentQueryDto;
import com.song.demo.entity.AppointmentPo;
import com.song.demo.vo.AppointmentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AppointmentMapper extends BaseMapper<AppointmentPo> {
    /**
     * 获取房屋当前预约时段
     * @param houseId
     * @return
     */
    List<AppointmentPo> getHouseDate(@Param("id") String houseId,
                                     @Param("startOfDay") LocalDateTime startOfDay,
                                     @Param("endOfDay") LocalDateTime endOfDay);

    List<AppointmentVo> queryAppointList(@Param("query") AppointmentQueryDto query);

    List<AppointmentVo> queryMyAppointments(@Param("userId") String userId);
}
