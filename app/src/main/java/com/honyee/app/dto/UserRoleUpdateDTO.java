package com.honyee.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class UserRoleUpdateDTO {
    @Schema(title = "用户id")
    private Long id;
    @Schema(title = "全量修改用户角色")
    private List<String> roleKeyList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getRoleKeyList() {
        return roleKeyList;
    }

    public void setRoleKeyList(List<String> roleKeyList) {
        this.roleKeyList = roleKeyList;
    }
}
