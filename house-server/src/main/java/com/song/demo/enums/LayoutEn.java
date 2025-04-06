package com.song.demo.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum LayoutEn {

    ONE(1, "one-bedroom"),

    TWO(2, "two-bedroom"),

    THREE(3, "three-bedroom"),

    FOUR(4, "four-bedroom");

    private Integer code;

    private String description;
}
