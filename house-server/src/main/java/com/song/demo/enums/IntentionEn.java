package com.song.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum IntentionEn {

    RENT("出租"),

    SALE("出售");

    private String description;
}
