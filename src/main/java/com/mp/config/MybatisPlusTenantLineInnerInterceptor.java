package com.mp.config;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class MybatisPlusTenantLineInnerInterceptor extends TenantLineInnerInterceptor {
    // 忽略租户注入的表名
    public static final Set<String> IGNORE_TABLE_NAME = new HashSet<>();

    public MybatisPlusTenantLineInnerInterceptor() {
        setTenantLineHandler(new MybatisPlusTenantHandler());

        // 通过实体上的注解添加 @InterceptorIgnore(tenantLine = "true")
        initIgnoreAnnTableName();
        // 手动添加
        IGNORE_TABLE_NAME.add("user");
    }

    /**
     * 初始化带有InterceptorIgnore的实体类
     */
    private void initIgnoreAnnTableName() {
        Reflections reflections = new Reflections(Constants.MODEL_PACKAGE);
        Set<Class<?>> annClassList = reflections.getTypesAnnotatedWith(InterceptorIgnore.class);
        for (Class<?> aClass : annClassList) {
            TableName tableNameAnn = aClass.getAnnotation(TableName.class);
            if (tableNameAnn != null) {
                String tableName = tableNameAnn.value();
                if (StringUtils.isNotBlank(tableName)) {
                    IGNORE_TABLE_NAME.add(tableName);
                }
            }
        }
    }

    /**
     * 解决多租户排除项支持Class和Method的需求
     */
    @Override
    public Expression buildTableExpression(Table table, Expression where, String whereSegment) {
        if (IGNORE_TABLE_NAME.contains(table.getName())) {
            return null;
        }
        boolean match = Stream.of(Thread.currentThread().getStackTrace())
            .filter(s -> s.toString().startsWith(Constants.BASE_PACKAGE))
            .anyMatch(s -> {
                try {
                    Class<?> aClass = Class.forName(s.getClassName());
                    Method declaredMethod = aClass.getDeclaredMethod(s.getMethodName());
                    return checkIgnore(aClass.getAnnotation(InterceptorIgnore.class))
                        || checkIgnore(declaredMethod.getAnnotation(InterceptorIgnore.class));
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    // ignore
                }
                return false;
            });

        if (match) {
            return null;
        }
        return super.buildTableExpression(table, where, whereSegment);
    }

    /**
     * 检查注解是否忽略租户注入
     * @return true 忽略
     */
    private boolean checkIgnore(InterceptorIgnore ann) {
        if (ann == null) {
            return false;
        }
        String value = ann.tenantLine();
        if (StringUtils.isBlank(value)) {
            return false;
        }
        if (StringPool.ONE.equals(value) || StringPool.TRUE.equals(value) || StringPool.ON.equals(value)) {
            return true;
        }
        if (StringPool.ZERO.equals(value) || StringPool.FALSE.equals(value) || StringPool.OFF.equals(value)) {
            return false;
        }
        throw ExceptionUtils.mpe("unsupported value \"%s\" by `@InterceptorIgnore", value);
    }

}
