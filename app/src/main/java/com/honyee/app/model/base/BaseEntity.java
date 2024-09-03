package com.honyee.app.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@InterceptorIgnore(tenantLine = "true")
public class BaseEntity {
    @Schema(title = "创建人")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    protected String createBy;

    @Schema(title = "创建时间")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    protected LocalDateTime createDate;

    @Schema(title = "更新人")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    protected String updateBy;

    @Schema(title = "更新时间")
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateDate;

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}
