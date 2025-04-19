package com.song.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 房屋审核状态
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum HouseAuditStatusEn {

    PENDING("待审核"),

    APPROVED("审核通过"),

    REJECTED("审核未通过");

    private String description;
}
