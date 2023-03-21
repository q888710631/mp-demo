package com.mp.model;

import com.baomidou.mybatisplus.annotation.*;
import com.mp.model.base.BaseEntity;

import java.util.Collection;

@TableName("user")
@InterceptorIgnore(tenantLine = "true")
public class User extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("nickname")
    private String nickname;

    @TableField("username")
    private String username;

    /**
     * bcrypt password
     */
    @TableField("password")
    private String password;

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
