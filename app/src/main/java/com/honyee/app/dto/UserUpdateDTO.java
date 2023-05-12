package com.honyee.app.dto;

import com.honyee.app.enums.UserStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(title = "用户DTO")
public class UserUpdateDTO {
    @NotNull(message = "用户ID不能为空")
    @Schema(title = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @NotBlank(message = "昵称不能为空")
    @Schema(title = "昵称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String nickname;

    @NotBlank(message = "用户名不能为空")
    @Schema(title = "用户名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(title = "密码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String password;

    @Schema(title = "状态")
    private UserStateEnum state;

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

    public UserStateEnum getState() {
        return state;
    }

    public void setState(UserStateEnum state) {
        this.state = state;
    }
}
