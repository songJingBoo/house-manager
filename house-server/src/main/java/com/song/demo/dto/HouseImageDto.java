package com.song.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class HouseImageDto implements Serializable {

    private String path;

    private Integer isCover;
}
