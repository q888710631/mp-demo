package com.honyee.app.model;

import com.baomidou.mybatisplus.annotation.*;
import com.honyee.app.config.mybatis.handler.ListRoleHandler;
import com.honyee.app.config.mybatis.handler.MybatisJsonTypeEntityHandler;
import com.honyee.app.model.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "人")
@TableName(value = "person", autoResultMap = true)
@InterceptorIgnore(tenantLine = "true")
public class Person extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("nickname")
    private String nickname;

    @TableField(value = "role_list", typeHandler = ListRoleHandler.class)
    private List<Role> roleList;

    @TableField(value = "role", typeHandler = MybatisJsonTypeEntityHandler.class)
    private Role role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
