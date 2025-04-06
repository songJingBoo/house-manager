package com.song.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVo {
    private Long id;
    private Long parentId;
    private String houseId;
    private String content;
    private String userId;
    private String username;
    private String replyName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;
}
