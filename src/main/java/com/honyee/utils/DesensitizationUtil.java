package com.honyee.utils;

/**
 * 脱敏工具
 */
public class DesensitizationUtil {

    private DesensitizationUtil() {}

    /**
     * 显示前n位与后m位，其余位数*替代
     *
     * @param beginLength 前n位
     * @param endLength   后m位
     */
    public static String hide(String value, int beginLength, int endLength) {
        if (value == null) {
            return null;
        }
        return value.replaceAll(String.format("(?<=.{%d}).(?=.{%d})", beginLength, endLength), "*");
    }

}
