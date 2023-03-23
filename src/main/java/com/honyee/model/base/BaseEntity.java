package com.honyee.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public class BaseEntity {
    @Schema(title = "创建人")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    protected String createBy;

    @Schema(title = "创建时间")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    protected Date createDate;

    @Schema(title = "更新人")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    protected String updateBy;

    @Schema(title = "更新时间")
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    protected Date updateDate;

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
