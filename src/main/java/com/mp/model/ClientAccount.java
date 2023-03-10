package com.mp.model;

import com.baomidou.mybatisplus.annotation.*;

@TableName("client_account")
public class ClientAccount extends BaseTenantEntity{
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("nick_name")
    private String nickName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
