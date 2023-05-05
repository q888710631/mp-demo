package com.honyee.app.utils;

import org.springframework.context.ApplicationContext;

public class SpringUtil {
    public static ApplicationContext CONTEXT;

    /**
     * 获取bean
     */
    public static <T> T getBean(Class<T> var1) {
        return CONTEXT.getBean(var1);
    }

    /**
     * 项目启动完毕判定
     * @return true 启动完毕
     */
    public static boolean isStartComplete() {
        return CONTEXT != null;
    }

}
