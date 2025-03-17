package com.song.demo.util;

import java.util.UUID;

public class UUUtil {

    public static String getId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
