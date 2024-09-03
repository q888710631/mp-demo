package com.honyee.app.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wu.dunhong
 */
public class StrUtil {

    private StrUtil() {
    }

    /**
     * unicode转字符串
     *
     * @param unicodeStr unicode
     * @return 字符串
     */
    public static String unicodeToString(String unicodeStr) {
        // XDigit是POSIX字符类，表示十六进制数字，\p{XDigit}等价于[a-fA-F0-9]
        // pattern用于匹配形如\\u6211的字符串
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(unicodeStr);
        char ch;
        while (matcher.find()) {
            // 捕获组按开括号'('从左到右编号（从1开始），以(A(B(C)))为例，group(1)表示(A(B(C))，group(2)表示(B(C))，group(3)表示(C)
            // group(2)表示第二个捕获组，即(\p{XDigit}{4})
            // Integer.parseInt(str, 16)把16进制的数字字符串转化为10进制，比如Integer.parseInt("16", 16) = 22
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            // 把第一个捕获组，即形如\\u6211这样的字符串替换成中文
            unicodeStr = unicodeStr.replace(matcher.group(1), ch + "");
        }
        return unicodeStr;
    }

    /**
     * 替换最后一次匹配的字符串
     */
    public static String replaceLast(String str, String source, String target) {
        StringBuilder result = new StringBuilder(str);
        int start = str.lastIndexOf(source);
        int end = start + source.length();
        result.replace(start, end, target);
        return result.toString();
    }

    /**
     * 获取标签对中的属性值
     * @param html 内容，例如 "<a href=\"链接A\" src=\"资源\"></a>超级<a href=\"链接B\"></a>"
     * @param attrName 属性明，例如 href
     * @return [链接A,链接B]
     */
    public static List<String> matchTagAttr(String html, String attrName) {
        List<String> list = new ArrayList<>();
        if (html == null) {
            return list;
        }
        // 标签对去除多余空格
        html = html.replaceAll("[ ]+", " ");
        Matcher srcMatcher = Pattern.compile("(?<=" + attrName + "=\")[^\">]*").matcher(html);
        while (srcMatcher.find()) {
            String group = srcMatcher.group();
            list.add(group);
        }
        return list;
    }
}
