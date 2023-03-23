package com.mp.dto;

import com.mp.dto.base.Insert;
import com.mp.dto.base.Update;
import com.mp.dto.base.UpdateUserRole;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;

@Schema(title = "用户DTO")
public class UserDTO {
    @NotNull(groups = {Update.class, UpdateUserRole.class})
    @Schema(title = "用户ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @NotNull(groups = {Insert.class})
    @NotBlank(message = "昵称不能改为空")
    @Schema(title = "昵称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String nickname;

    @NotNull(groups = {Insert.class})
    @NotBlank(message = "用户名不能改为空")
    @Schema(title = "用户名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;

    @NotNull(groups = {Insert.class})
    @NotBlank(message = "密码不能改为空")
    @Schema(title = "密码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String password;

    @Schema(title = "角色key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
