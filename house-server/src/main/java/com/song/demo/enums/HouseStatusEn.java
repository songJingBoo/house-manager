package com.song.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 房屋状态
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum HouseStatusEn {

    UNPUBLISHED("待发布"),

    PUBLISHED("已发布"),

    FINISHED("已出售/出租"),

    REMOVED("已下架");

//    UNAUDITED("待审核"),
//
//    UNPUBLISHED("待发布"),
//
//    PUBLISHED("已发布"),
//
//    FINISHED("已出售/出租"),
//
//    REMOVED("已下架"),
//
//    REJECTED("审核未通过");

    private String description;
}
