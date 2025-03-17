package com.song.demo.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class LoginVo {
    private String username;
    private String userId;
    private String token;
    private Date expirationTime;
}
