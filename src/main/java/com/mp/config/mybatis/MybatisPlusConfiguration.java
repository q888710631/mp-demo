package com.mp.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.mp.config.Constants;
import com.mp.config.TenantHelper;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@MapperScan(Constants.MAPPER_PACKAGE)
public class MybatisPlusConfiguration implements MetaObjectHandler {

    /**
     * insert时注入字段内容
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName(Constants.FIELD_CREATE_TIME, new Date(), metaObject);
        this.setFieldValByName(Constants.FIELD_UPDATE_TIME, new Date(), metaObject);
        Long tenantId = TenantHelper.getTenantId();
        if (tenantId != null) {
            // todo
        }
    }

    /**
     * update时注入字段内容
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName(Constants.FIELD_UPDATE_TIME, new Date(), metaObject);
    }

    /**
     * 拦截器配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new MybatisPlusTenantLineInnerInterceptor()); // 多租户
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 分页
        return interceptor;
    }

}
