package com.honyee.app.utils;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.DataBinder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class HttpUtil {

    private HttpUtil() {
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

    /**
     * 读取url的query参数，并转成dto
     */
    public static <T> T readQuery(String url, Class<T> clz) throws ReflectiveOperationException {
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromHttpUrl(url).build().getQueryParams();
        Map<String, Object> parameters = getParameters(queryParams);
        return DataBindUtil.bind(clz, parameters);
    }

    /**
     * 将MultiValueMap转成MutablePropertyValues可用的Map
     */
    private static Map<String, Object> getParameters(MultiValueMap<String, String> map) {
        Map<String, Object> params = new TreeMap<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String paramName = DataBindUtil.toCamelCase(entry.getKey());
            List<String> values = entry.getValue();
            if (values == null || values.isEmpty()) {
                // Do nothing, no values found at all.
            } else if (values.size() > 1) {
                params.put(paramName, values);
            } else {
                params.put(paramName, values.get(0));
            }
        }
        return params;
    }


}
