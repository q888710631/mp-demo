package com.honyee.app.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.honyee.app.model.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "用户角色")
@TableName("user_role")
public class UserRole extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long roleId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
