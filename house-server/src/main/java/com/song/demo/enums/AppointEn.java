package com.song.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppointEn {

    PENDING("待确认"),

    CONFIRMED("已确认"),

    CANCELLED("已取消");

    private String description;
}
