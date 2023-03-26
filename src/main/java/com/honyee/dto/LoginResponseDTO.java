package com.honyee.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginResponseDTO {
    @Schema(title = "令牌")
    private String token;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "token='" + token + '\'' +
                '}';
    }
}
