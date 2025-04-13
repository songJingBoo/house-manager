package com.song.demo.dto.query;

import com.song.demo.dto.HouseFilterDto;
import com.song.demo.enums.AppointEn;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Data
public class AppointmentQueryDto {

    private String title;

    private String startTime;

    private String endTime;

    @Enumerated(EnumType.STRING)
    private AppointEn status;
}
