package com.song.demo.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.song.demo.common.BizException;
import com.song.demo.dto.AppointCreateDto;
import com.song.demo.dto.MessageReadDto;
import com.song.demo.entity.AppointmentPo;
import com.song.demo.entity.MessagePo;
import com.song.demo.enums.AppointEn;
import com.song.demo.mapper.AppointmentMapper;
import com.song.demo.mapper.MessageMapper;
import com.song.demo.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 用户获取消息
     * @return
     */
    @GetMapping("/get")
    public List<MessagePo> getMessage(@RequestParam(value = "isRead", required = false) Integer isRead) {
        return messageMapper.selectList(Wrappers.<MessagePo>lambdaQuery()
                .eq(MessagePo::getUserId, SecurityUtils.getUserId())
                .eq(ObjectUtil.isNotNull(isRead), MessagePo::getIsRead, isRead));
    }

    /**
     * 用户消息设为已读
     * @return
     */
    @PostMapping("/read")
    public void readMessage(@RequestBody @Valid MessageReadDto messageReadDto) {
        String ids = messageReadDto.getIds().stream().map(String::valueOf).collect(Collectors.joining(","));
        messageMapper.readMessage(ids);
    }
}
