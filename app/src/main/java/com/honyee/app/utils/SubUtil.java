package com.honyee.app.utils;

import java.util.ArrayList;
import java.util.List;

public class SubUtil {

    /**
     * list 分页处理
     *
     * @param pageNum 从0开始
     */
    public static <T> List<T> pageList(List<T> list, int pageNum, int pageSize) {
        // 避免负数
        pageNum = Math.max(pageNum, 0);
        pageSize = Math.max(pageSize, 0);

        int begin = pageNum * pageSize;
        int end = (pageNum + 1) * pageSize;

        if (list == null || list.isEmpty() || begin > list.size()) {
            return new ArrayList<>();
        }

        if (end > list.size()) {
            end = list.size();
        }
        return list.subList(begin, end);
    }

    /**
     * list range处理
     *
     * @param fromIndex 从0开始
     * @param length    长度
     */
    public static <T> List<T> subList(List<T> list, int fromIndex, int length) {
        // 避免负数
        fromIndex = Math.max(fromIndex, 0);
        length = Math.max(length, 0);
        int size = list.size();
        if (fromIndex > size) {
            return new ArrayList<>();
        }

        int toIndex = Math.min(fromIndex + length, size);

        // 避免负数
        return list.subList(fromIndex, toIndex);
    }

    /**
     * string substring
     *
     * @param fromIndex 从0开始
     */
    public static String subString(String str, int fromIndex, int length) {
        // 避免负数
        fromIndex = Math.max(fromIndex, 0);
        length = Math.max(length, 0);
        int size = str.length();
        if (fromIndex > size) {
            return "";
        }

        int toIndex = Math.min(fromIndex + length, size);

        // 避免负数
        return str.substring(fromIndex, toIndex);
    }

    public static final String CONTENT_SUFFIX = "...";

    /**
     * 摘要&后缀
     */
    public static String subStringWithSuffix(String str, int length) {
        if (str == null) {
            return null;
        }
        String tmp = str;
        if (str.length() > length) {
            tmp = subString(str, 0, length) + CONTENT_SUFFIX;
        }
        return tmp;
    }

}
