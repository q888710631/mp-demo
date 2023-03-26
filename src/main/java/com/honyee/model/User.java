package com.honyee.model;

import com.baomidou.mybatisplus.annotation.*;
import com.honyee.enums.UserStateEnum;
import com.honyee.model.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Schema(title = "用户（租户）")
@TableName("user")
@InterceptorIgnore(tenantLine = "true")
public class User extends BaseEntity implements UserDetails {
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

    @Schema(title = "状态")
    @TableField("state")
    private UserStateEnum state;

    @Schema(title = "锁定开始时间")
    @TableField("lock_begin_date")
    private LocalDateTime lockBeginDate;

    @Schema(title = "锁定结束时间")
    @TableField("lock_end_date")
    private LocalDateTime lockEndDate;

    @Schema(title = "角色")
    @TableField(exist = false)
    private Collection<Role> roles;

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

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
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

    public UserStateEnum getState() {
        return state;
    }

    public void setState(UserStateEnum state) {
        this.state = state;
    }

    public LocalDateTime getLockBeginDate() {
        return lockBeginDate;
    }

    public void setLockBeginDate(LocalDateTime lockBeginDate) {
        this.lockBeginDate = lockBeginDate;
    }

    public LocalDateTime getLockEndDate() {
        return lockEndDate;
    }

    public void setLockEndDate(LocalDateTime lockEndDate) {
        this.lockEndDate = lockEndDate;
    }

    @Transient
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return List.of();
        }
        return this.roles.stream().map(Role::getRoleName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (this.lockBeginDate != null && this.lockEndDate != null) {
            LocalDateTime now = LocalDateTime.now();
            return !(now.isAfter(this.lockBeginDate) && now.isBefore(this.lockEndDate));
        }
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.state == UserStateEnum.ENABLE;
    }
}
