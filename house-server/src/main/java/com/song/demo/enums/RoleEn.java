package com.song.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RoleEn {

    CUSTOMER("Customer", "客户"),

    AGENT("Agent", "经纪人"),

    REVIEWER("Reviewer", "审核员"),

    ADMIN("Admin", "系统管理员");

    private String role;

    private String description;
}
