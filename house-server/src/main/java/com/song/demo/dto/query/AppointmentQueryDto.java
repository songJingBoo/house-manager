package com.song.demo.dto.query;

import com.song.demo.enums.AppointEn;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class AppointmentQueryDto {

    private String title;

    private String startTime;

    private String endTime;

    @Enumerated(EnumType.STRING)
    private AppointEn status;
}
