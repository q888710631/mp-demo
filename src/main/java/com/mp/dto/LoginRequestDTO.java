package com.mp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public class LoginRequestDTO {
    @NotBlank(message = "用户名不能为空")
    @Schema(title = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(title = "密码")
    private String password;

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
}
