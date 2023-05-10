package com.honyee.app.model;

import com.baomidou.mybatisplus.annotation.*;
import com.honyee.app.enums.InPatientStateEnum;
import com.honyee.app.model.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(title = "住院记录")
@TableName("inpatient_record")
@InterceptorIgnore(tenantLine = "true")
public class InpatientRecord extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(title = "入院日期")
    @TableField("enter_date")
    private LocalDate enterDate;

    @Schema(title = "出院日期")
    @TableField("out_date")
    private LocalDateTime outDate;

    @Schema(title = "入院原因")
    @TableField("enter_reason")
    private String enterReason;

    @Schema(title = "床号")
    @TableField("bed_number")
    private String bedNumber;

    @Schema(title = "患者姓名")
    @TableField("patient_name")
    private String patientName;

    @Schema(title = "联系人姓名")
    @TableField("contact_name")
    private String contactName;

    @Schema(title = "联系人关系")
    @TableField("contact_relation")
    private String contactRelation;

    @Schema(title = "联系人所在地区")
    @TableField("contact_area")
    private String contactArea;

    @Schema(title = "联系人手机号1")
    @TableField("contact_phone_1")
    private String contactPhone1;

    @Schema(title = "联系人手机号2")
    @TableField("contact_phone_2")
    private String contactPhone2;

    @Schema(title = "状态")
    @TableField("state")
    private InPatientStateEnum state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getEnterDate() {
        return enterDate;
    }

    public void setEnterDate(LocalDate enterDate) {
        this.enterDate = enterDate;
    }

    public String getEnterReason() {
        return enterReason;
    }

    public void setEnterReason(String enterReason) {
        this.enterReason = enterReason;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactRelation() {
        return contactRelation;
    }

    public void setContactRelation(String contactRelation) {
        this.contactRelation = contactRelation;
    }

    public String getContactArea() {
        return contactArea;
    }

    public void setContactArea(String contactArea) {
        this.contactArea = contactArea;
    }

    public String getContactPhone1() {
        return contactPhone1;
    }

    public void setContactPhone1(String contactPhone1) {
        this.contactPhone1 = contactPhone1;
    }

    public String getContactPhone2() {
        return contactPhone2;
    }

    public void setContactPhone2(String contactPhone2) {
        this.contactPhone2 = contactPhone2;
    }

    public InPatientStateEnum getState() {
        return state;
    }

    public void setState(InPatientStateEnum state) {
        this.state = state;
    }

    public LocalDateTime getOutDate() {
        return outDate;
    }

    public void setOutDate(LocalDateTime outDate) {
        this.outDate = outDate;
    }
}
