package com.song.demo.dto;

import com.song.demo.enums.IntentionEn;
import com.song.demo.enums.LayoutEn;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建房源 操作
 */
@Data
public class HousePublishDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "户型不能为空")
    @Enumerated(EnumType.STRING)
    private LayoutEn layout;

    @NotNull(message = "面积不能为空")
    private BigDecimal area;

    @NotNull(message = "楼层不能为空")
    private Integer floor;

    @NotEmpty(message = "城市不能为空")
    private String city;

    @NotEmpty(message = "小区不能为空")
    private String community;

    @NotEmpty(message = "详细地址不能为空")
    private String address;

    @NotNull(message = "期望售价不能为空")
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal expectPrice;

    @NotNull(message = "联系人不能为空")
    private String name;

    @NotNull(message = "联系电话不能为空")
    private String phone;

    @NotNull(message = "交易类型不能为空")
    @Enumerated(EnumType.STRING)
    private IntentionEn intention;

    @NotNull(message = "房屋图片不能为空")
    private List<HouseImageDto> files;
}
