package com.honyee.app.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.honyee.app.config.Constants;
import com.honyee.app.config.TenantHelper;
import com.honyee.app.enums.UserStateEnum;
import com.honyee.app.model.User;
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

        Object originalObject = metaObject.getOriginalObject();
        if (originalObject instanceof User) {
            User user = (User) originalObject;
            if (user.getState() == null) {
                user.setState(UserStateEnum.ENABLE);
            }
        }

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