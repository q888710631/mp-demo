package com.honyee.app.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.honyee.app.enums.StateEnum;
import com.honyee.app.model.base.BaseTenantEntity;

@TableName("product")
public class Product extends BaseTenantEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private StateEnum state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }
}
