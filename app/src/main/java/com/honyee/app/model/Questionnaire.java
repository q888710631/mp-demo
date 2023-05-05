package com.honyee.app.model;

import com.baomidou.mybatisplus.annotation.*;
import com.honyee.app.model.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "问卷")
@TableName("questionnaire")
@InterceptorIgnore(tenantLine = "true")
public class Questionnaire extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(title = "姓名")
    @TableField("full_name")
    private String fullName;

    @Schema(title = "电话")
    @TableField("phone_number")
    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
