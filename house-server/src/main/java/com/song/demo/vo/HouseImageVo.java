package com.song.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HouseImageVo {
    private String houseId;
    private String imageUrl;
    private Integer isCover;

}
