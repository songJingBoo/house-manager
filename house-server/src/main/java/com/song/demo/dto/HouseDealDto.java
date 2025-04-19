package com.song.demo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HouseDealDto implements Serializable {

    private String houseId;

    private BigDecimal finalPrice;
}
