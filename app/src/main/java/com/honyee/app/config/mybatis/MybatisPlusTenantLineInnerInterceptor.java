package com.honyee.app.config.mybatis;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.honyee.app.config.Constants;
import com.honyee.app.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import org.reflections.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
public class MybatisPlusTenantLineInnerInterceptor extends TenantLineInnerInterceptor {

    // 忽略租户注入的表名
    public static final Set<String> IGNORE_TABLE_NAME = new HashSet<>();

    public MybatisPlusTenantLineInnerInterceptor() {
        setTenantLineHandler(new MybatisPlusTenantHandler());

        // 通过实体上的注解添加 @InterceptorIgnore(tenantLine = "true")
        initIgnoreAnnTableName();
        // 手动添加
        IGNORE_TABLE_NAME.add("user");
        log.info("忽略租户注入的表：{}", String.join("、", IGNORE_TABLE_NAME));
    }

    /**
     * 初始化带有InterceptorIgnore的实体类
     */
    private void initIgnoreAnnTableName() {
        Set<Class<?>> annClassList = ClassUtil.getTypesAnnotatedWith(Constants.MODEL_PACKAGE, InterceptorIgnore.class);
        for (Class<?> aClass : annClassList) {
            TableName tableNameAnn = aClass.getAnnotation(TableName.class);
            if (tableNameAnn != null && StringUtils.isNotBlank(tableNameAnn.value())) {
                Annotation ann = ReflectionUtils.getAllAnnotations(aClass, annotation -> annotation.annotationType() == InterceptorIgnore.class)
                    .stream()
                    .findFirst()
                    .orElseThrow();
                boolean match = "true".equals(((InterceptorIgnore) ann).tenantLine());
                if (match) {
                    IGNORE_TABLE_NAME.add(tableNameAnn.value());
                }
            }
        }
    }

    /**
     * 解决多租户排除项支持Class和Method的需求
     */
    @Override
    public Expression buildTableExpression(Table table, Expression where, String whereSegment) {
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
     *
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
