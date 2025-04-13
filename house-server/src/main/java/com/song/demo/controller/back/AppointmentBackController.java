package com.song.demo.controller.back;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.song.demo.common.BizException;
import com.song.demo.dto.AppointCreateDto;
import com.song.demo.dto.query.AppointmentQueryDto;
import com.song.demo.entity.AppointmentPo;
import com.song.demo.enums.AppointEn;
import com.song.demo.mapper.AppointmentMapper;
import com.song.demo.util.SecurityUtils;
import com.song.demo.vo.AppointmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/back/appoint")
public class AppointmentBackController {

    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 获取预约列表
     * @param appointmentQueryDto
     * @return
     */
    @PostMapping("/queryAppointList")
    public List<AppointmentVo> queryAppointList(@RequestBody AppointmentQueryDto appointmentQueryDto) {
        return appointmentMapper.queryAppointList(appointmentQueryDto);
    }
}
