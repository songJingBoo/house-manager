package com.song.demo.controller.back;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.song.demo.common.BizException;
import com.song.demo.dto.AppointAuditDto;
import com.song.demo.dto.AppointCreateDto;
import com.song.demo.dto.AuditDto;
import com.song.demo.dto.query.AppointmentQueryDto;
import com.song.demo.entity.AppointmentPo;
import com.song.demo.entity.HousePo;
import com.song.demo.entity.MessagePo;
import com.song.demo.enums.AppointEn;
import com.song.demo.mapper.AppointmentMapper;
import com.song.demo.mapper.MessageMapper;
import com.song.demo.util.SecurityUtils;
import com.song.demo.vo.AppointmentVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
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

    @Autowired
    private MessageMapper messageMapper;

    @ApiOperation("获取预约列表")
    @PostMapping("/queryAppointList")
    public List<AppointmentVo> queryAppointList(@RequestBody AppointmentQueryDto appointmentQueryDto) {
        return appointmentMapper.queryAppointList(appointmentQueryDto);
    }

    @ApiOperation("审核预约")
    @PostMapping("/auditAppoint")
    public void changeStatus(@RequestBody @Valid AppointAuditDto appointAuditDto) {
        AppointmentPo appointmentPo = appointmentMapper.selectById(appointAuditDto.getId());
        if (appointmentPo == null) {
            throw new BizException("预约记录不存在");
        }

        appointmentPo.setStatus(appointAuditDto.getStatus());
        int count = appointmentMapper.updateById(appointmentPo);
        if (count == 0) {
            throw new BizException("预约审核失败");
        }

        // 发送通知 - 预约
        MessagePo messagePo = new MessagePo();
        messagePo.setHouseId(appointmentPo.getHouseId());
        messagePo.setUserId(appointmentPo.getUserId());
        if (appointmentPo.getStatus().equals(AppointEn.CONFIRMED)) {
            messagePo.setTitle("预约审核已通过");
            messagePo.setContent("您预约的看房申请已通过审核，请按预约时间准时前往~");
        } else {
            messagePo.setTitle("预约审核已取消");
            messagePo.setContent("您预约的看房申请被取消，如有疑问请联系房屋对应代理人~");
        }
        messagePo.setCreator(SecurityUtils.getUserId());
        messageMapper.insert(messagePo);
    }
}
