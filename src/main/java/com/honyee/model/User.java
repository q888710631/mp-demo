package com.honyee.model;

import com.baomidou.mybatisplus.annotation.*;
import com.honyee.model.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;

@Schema(title = "用户（租户）")
@TableName("user")
@InterceptorIgnore(tenantLine = "true")
public class User extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(title = "昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(title = "用户名")
    @TableField("username")
    private String username;

    @Schema(title = "密码 bcrypt加密过")
    @TableField("password")
    private String password;

    @Schema(title = "角色")
    @TableField(exist = false)
    Collection<Role> roles;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
}
