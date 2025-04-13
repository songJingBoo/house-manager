package com.song.demo.controller;

import cn.hutool.core.net.URLDecoder;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.song.demo.common.BizException;
import com.song.demo.dto.AppointCreateDto;
import com.song.demo.dto.query.AppointmentQueryDto;
import com.song.demo.dto.query.HouseQueryDto;
import com.song.demo.entity.AppointmentPo;
import com.song.demo.entity.HouseFilterPo;
import com.song.demo.enums.AppointEn;
import com.song.demo.mapper.AppointmentMapper;
import com.song.demo.mapper.HouseImageMapper;
import com.song.demo.util.FileUploadUtil;
import com.song.demo.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/appoint")
public class AppointmentController {

    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 获取房屋某日已有预约
     * @param houseId
     * @return
     */
    @GetMapping("/queryHouseDate")
    public List<AppointmentPo> getHouseDate(@RequestParam("houseId") String houseId,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusSeconds(1);
        return appointmentMapper.getHouseDate(houseId, startOfDay, endOfDay);
    }

    /**
     * 预约看房
     * @return
     */
    @PostMapping("/createAppoint")
    public void createAppointment(@RequestBody @Valid AppointCreateDto appointCreateDto) {
        AppointmentPo hasAlready = appointmentMapper.selectOne(Wrappers.<AppointmentPo>lambdaQuery()
                .eq(AppointmentPo::getHouseId, appointCreateDto.getHouseId())
                .eq(AppointmentPo::getUserId, SecurityUtils.getUserId())
                .eq(AppointmentPo::getStatus, AppointEn.PENDING)
                .or()
                .eq(AppointmentPo::getStatus, AppointEn.CONFIRMED));
        if (hasAlready != null) {
            throw new BizException("请勿重复预约");
        }

        AppointmentPo appointmentPo = new AppointmentPo();
        appointmentPo.setUserId(SecurityUtils.getUserId());
        appointmentPo.setHouseId(appointCreateDto.getHouseId());
        appointmentPo.setStartTime(appointCreateDto.getStartTime());
        appointmentPo.setEndTime(appointCreateDto.getEndTime());
        try {
            appointmentMapper.insert(appointmentPo);
        } catch (DataIntegrityViolationException e) {
            throw new BizException("该时段已被预约");
        }
    }

    /**
     * 取消预约
     * @return
     */
    @DeleteMapping("/deleteAppoint")
    public void deleteAppoint(@RequestParam("appointId") Long appointId) {
        AppointmentPo appointmentPo = appointmentMapper.selectById(appointId);
        if (appointmentPo == null) {
            throw new BizException("预约记录不存在，或已被删除！");
        }
        appointmentMapper.deleteById(appointId);
    }
}
