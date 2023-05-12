package com.honyee.app.dto;

import com.honyee.app.enums.UserStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(title = "用户DTO")
public class UserCreateDTO {

    @NotNull(message = "昵称不能为空")
    @NotBlank(message = "昵称不能为空")
    @Schema(title = "昵称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;

    @NotNull(message = "用户名不能为空")
    @NotBlank(message = "用户名不能为空")
    @Schema(title = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    @Schema(title = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(title = "状态")
    private UserStateEnum state;

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
