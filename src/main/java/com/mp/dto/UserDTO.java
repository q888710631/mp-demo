package com.mp.dto;

import com.mp.dto.base.Insert;
import com.mp.dto.base.Update;
import com.mp.dto.base.UpdateUserRole;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;

public class UserDTO {
    @NotNull(groups = {Update.class, UpdateUserRole.class})
    private Long id;

    @NotNull(groups = {Insert.class})
    @NotBlank(message = "昵称不能改为空")
    private String nickname;

    @NotNull(groups = {Insert.class})
    @NotBlank(message = "用户名不能改为空")
    private String username;

    @NotNull(groups = {Insert.class})
    @NotBlank(message = "密码不能改为空")
    private String password;

    /**
     * role_key list
     */
    private Collection<String> roles;

    public UserDTO() {
        this.roles = new HashSet<>();
    }

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

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }
}
