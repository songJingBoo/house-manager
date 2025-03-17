package com.song.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEn {

    Customer("客户"),

    Agent("经纪人"),

    Reviewer("审核员"),

    Admin("系统管理员");

    private String description;
}
