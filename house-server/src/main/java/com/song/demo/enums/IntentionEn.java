package com.song.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IntentionEn {

    RENT("出租"),

    SALE("出售");

    private String description;
}
