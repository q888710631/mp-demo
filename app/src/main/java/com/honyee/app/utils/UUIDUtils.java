package com.honyee.app.utils;

import java.util.UUID;

/**
 * 生成UUID
 */
public class UUIDUtils {

    private UUIDUtils() {
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
