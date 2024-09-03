package com.honyee.app.model.test;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.honyee.app.config.mybatis.handler.ListHumanHandler;
import com.honyee.app.config.mybatis.handler.MybatisJsonTypeEntityHandler;
import com.honyee.app.model.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "人")
@TableName(value = "person", autoResultMap = true)
public class Person extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("nickname")
    private String nickname;

    @TableField(value = "role_list", typeHandler = ListHumanHandler.class)
    private List<Human> roleList;

    @TableField(value = "role", typeHandler = MybatisJsonTypeEntityHandler.class)
    private Human role;

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

    public List<Human> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Human> roleList) {
        this.roleList = roleList;
    }

    public Human getRole() {
        return role;
    }

    public void setRole(Human role) {
        this.role = role;
    }
}
