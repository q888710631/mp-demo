package com.mp.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HttpUtils {

    private HttpUtils() {
    }

    private static final String UNKNOWN = "unknown";

    private static final String SEPARATE_CHARACTER = ",";

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (isIpInvalid(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (isIpInvalid(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isIpInvalid(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isIpInvalid(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isIpInvalid(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isIpInvalid(ip)) {
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    //ignore
                }
                ip = inet == null ? null : inet.getHostAddress();
            }
        }
        return handleMultiIp(ip);
    }

    private static String handleMultiIp(String ip) {
        if (StringUtils.isNotBlank(ip) && ip.contains(SEPARATE_CHARACTER)) {
            ip = ip.split(SEPARATE_CHARACTER)[0];
        }
        return ip;
    }

    private static boolean isIpInvalid(String ip){
        return StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip);
    }

}
