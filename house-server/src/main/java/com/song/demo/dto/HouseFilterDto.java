package com.song.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 客户端 房屋多条件过滤
 */
@Data
public class HouseFilterDto implements Serializable {

    private String code;

    private List<condition> checked;

    @Data
    public static class condition implements Serializable {

        private Integer min;

        private Integer eql;

        private Integer max;
    }
}
