package com.honyee.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Schema(title = "住院记录")
public class InpatientRecordCreateDTO {

    @Schema(title = "入院日期")
    private LocalDate enterDate;

    @Schema(title = "入院原因")
    @Size(max = 200, message = "入院原因最多200个字")
    private String enterReason;

    @Schema(title = "床号")
    @NotNull(message = "床号不能为空")
    @NotBlank(message = "床号不能为空")
    @Size(max = 32, message = "床号最多32个字")
    private String bedNumber;

    @Schema(title = "患者姓名")
    @NotNull(message = "患者姓名不能为空")
    @NotBlank(message = "患者姓名不能为空")
    @Size(max = 32, message = "患者姓名最多32个字")
    private String patientName;

    @Schema(title = "联系人姓名")
    @NotNull(message = "联系人姓名不能为空")
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 32, message = "联系人姓名最多32个字")
    private String contactName;

    @Schema(title = "同患者关系")
    @Size(max = 32, message = "同患者关系最多32个字")
    private String contactRelation;

    @Schema(title = "联系人所在地区")
    @Size(max = 32, message = "所在地区最多32个字")
    private String contactArea;

    @Schema(title = "联系人手机号1")
    @NotNull(message = "手机/固话1不能为空")
    @NotBlank(message = "手机/固话1不能为空")
    @Size(max = 32, message = "联系人姓名最多32个字")
    private String contactPhone1;

    @Schema(title = "联系人手机号2")
    @NotBlank(message = "手机/固话1不能为空")
    @Size(max = 32, message = "联系人姓名最多32个字")
    private String contactPhone2;

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

}
