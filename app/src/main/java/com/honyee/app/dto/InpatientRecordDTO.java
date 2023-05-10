package com.honyee.app.dto;

import com.honyee.app.enums.InPatientStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(title = "住院记录")
public class InpatientRecordDTO {
    private Long id;

    @Schema(title = "入院日期")
    private LocalDate enterDate;

    @Schema(title = "出院日期")
    private LocalDateTime outDate;

    @Schema(title = "入院原因")
    private String enterReason;

    @Schema(title = "床号")
    private String bedNumber;

    @Schema(title = "患者姓名")
    private String patientName;

    @Schema(title = "联系人姓名")
    private String contactName;

    @Schema(title = "同患者关系")
    private String contactRelation;

    @Schema(title = "联系人所在地区")
    private String contactArea;

    @Schema(title = "联系人手机号1")
    private String contactPhone1;

    @Schema(title = "联系人手机号2")
    private String contactPhone2;

    @Schema(title = "状态")
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
