package com.honyee.app.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.honyee.app.config.Constants;
import com.honyee.app.enums.UserStateEnum;
import com.honyee.app.model.User;
import com.honyee.app.model.base.BaseEntity;
import com.honyee.app.model.base.BaseTenantEntity;
import com.honyee.app.utils.TenantHelper;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@MapperScan(Constants.MAPPER_PACKAGE)
public class MybatisPlusConfiguration implements MetaObjectHandler {

    /**
     * insert时注入字段内容
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName(Constants.FIELD_CREATE_DATE, LocalDateTime.now(), metaObject);
        this.setFieldValByName(Constants.FIELD_UPDATE_DATE, LocalDateTime.now(), metaObject);

        // entity
        Object originalObject = metaObject.getOriginalObject();

        Long tenantId = TenantHelper.getTenantId();
        if (tenantId != null) {
            if (originalObject instanceof BaseTenantEntity) {
                BaseTenantEntity baseTenantEntity = (BaseTenantEntity) originalObject;
                if (baseTenantEntity.getTenantId() == null) {
                    // 填充租户
                    baseTenantEntity.setTenantId(tenantId);
                }
            }
            if (originalObject instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) originalObject;
                if (baseEntity.getCreateBy() == null) {
                    baseEntity.setCreateBy(tenantId.toString());
                }
                baseEntity.setUpdateBy(tenantId.toString());
            }

        }

        // User填充默认state
        if (originalObject instanceof User) {
            User user = (User) originalObject;
            if (user.getState() == null) {
                user.setState(UserStateEnum.ENABLE);
            }
        }
    }

    /**
     * update时注入字段内容
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName(Constants.FIELD_UPDATE_DATE, LocalDateTime.now(), metaObject);

        // entity
        Object originalObject = metaObject.getOriginalObject();

        Long tenantId = TenantHelper.getTenantId();
        if (tenantId != null) {
            if (originalObject instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) originalObject;
                baseEntity.setUpdateBy(tenantId.toString());
            }
        }
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
