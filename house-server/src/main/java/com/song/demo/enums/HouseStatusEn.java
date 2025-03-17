package com.song.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HouseStatusEn {

    PENDING("待审核"),

    AVAILABLE("已发布"),

    RENTED("已出租"),

    SOLD("已出售"),

    UNAVAILABLE("已下架");

    private String description;
}
