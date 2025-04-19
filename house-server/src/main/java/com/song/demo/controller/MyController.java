package com.song.demo.controller;

import com.song.demo.mapper.AppointmentMapper;
import com.song.demo.mapper.HouseMapper;
import com.song.demo.util.SecurityUtils;
import com.song.demo.vo.AppointmentVo;
import com.song.demo.vo.HouseVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my")
public class MyController {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @ApiOperation("查询我发布的房屋")
    @GetMapping("/houses")
    public List<HouseVo> getMyHouses() {
        return houseMapper.queryMyHouse(SecurityUtils.getUserId());
    }

    @ApiOperation("查询我关注的房屋")
    @GetMapping("/likes")
    public List<HouseVo> getMyLikeHouses() {
        return houseMapper.queryMyLikeHouse(SecurityUtils.getUserId());
    }

    @ApiOperation("查询我发起的预约")
    @GetMapping("/appoints")
    public List<AppointmentVo> getMyAppointments() {
        return appointmentMapper.queryMyAppointments(SecurityUtils.getUserId());
    }
}
