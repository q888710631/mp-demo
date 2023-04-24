package com.honyee.app.utils;

import org.noear.snack.ONode;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class JsonUtil {

    /**
     * 参考
     * <pre> <a href="https://gitee.com/noear/snack3">noear / snack3</a></pre>
     * 合并json对象
     *
     * @param source 源对象
     * @param target 目标对象
     * @param paths  指定目标，为空则执行全覆盖
     * @return 合并结果
     */
    public static String merge(@NotNull String source, @NotNull String target, @Nullable String... paths) {
        ONode sourceNode = ONode.loadStr(source);
        ONode targetNode = ONode.loadStr(target);
        // paths为空则执行全覆盖
        if (paths == null || paths.length == 0) {
            paths = sourceNode.obj().keySet().stream().map(key -> "$." + key).toArray(String[]::new);
        }
        for (String path : paths) {
            // selectOrNew避免对应path没有value
            targetNode.selectOrNew(path).val(sourceNode.select(path));
        }
        return targetNode.toJson();
    }
}
