package com.song.demo.dto;

import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CommentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "房源id不能为空")
    private String houseId;

    /**
     * 父评论id
     */
    private Integer commentId;

    @NotEmpty(message = "评论内容不能为空")
    private String content;
}
