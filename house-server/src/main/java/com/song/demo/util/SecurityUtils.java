package com.song.demo.util;

import com.song.demo.service.impl.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    // 获取当前用户
    public static CustomUserDetails getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    // 获取当前username
    public static String getUsername() {
        return getUser().getUsername();
    }

    // 获取当前userid
    public static String getUserId() {
        return getUser().getUserId();
    }
}
