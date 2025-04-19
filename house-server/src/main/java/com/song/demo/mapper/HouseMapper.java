package com.song.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.demo.dto.query.HouseBackQueryDto;
import com.song.demo.dto.query.HouseQueryDto;
import com.song.demo.entity.HousePo;
import com.song.demo.vo.AppointmentVo;
import com.song.demo.vo.HouseVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface HouseMapper extends BaseMapper<HousePo> {

    /**
     * 管理端获取房屋列表
     * @param query
     * @return
     */
    List<HouseVo> getHouse(@Param("query") HouseBackQueryDto query);

    /**
     * 管理端获取审核房屋列表
     * @param query
     * @return
     */
    List<HouseVo> getAuditHouse(@Param("query") HouseBackQueryDto query);


    /**
     * 客户端获取房源列表
     * @param query
     * @return
     */
    List<HouseVo> queryHouse(@Param("query") HouseQueryDto query);

    /**
     * 获取我的房屋
     * @return
     */
    List<HouseVo> queryMyHouse(@Param("userId") String userId);

    /**
     * 获取我的关注房屋
     * @return
     */
    List<HouseVo> queryMyLikeHouse(@Param("userId") String userId);

    /**
     * 获取我的关注房屋
     * @return
     */
    List<AppointmentVo> getMyAppointments(@Param("userId") String userId);

    /**
     * 获取热门房屋
     * @return
     */
    List<HouseVo> queryHotHouse();

    /**
     * 获取房源详情
     * @param id
     * @return
     */
    HouseVo getHouseById(@Param("id") String id);

    /**
     * 获取房源详情
     * @param id
     * @return
     */
    HouseVo getHouseById(@Param("id") String id, @Param("userId") String userId);

    /**
     * 房屋完成交易
     * @param houseId
     * @param finalPrice
     * @return
     */
    @Update("update houses set status = 'FINISHED', final_price = ${finalPrice} where house_id = #{houseId}")
    int dealHouse(@Param("houseId") String houseId, @Param("finalPrice") BigDecimal finalPrice);

    /**
     * 房屋发布/下架
     * @param houseId
     * @return
     */
    @Update("update houses set status = #{status} where house_id = #{houseId}")
    int changeStatus(@Param("houseId") String houseId, @Param("status") String status);
}
