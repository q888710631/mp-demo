package com.honyee.app.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.honyee.app.enums.InPatientStateEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InpatientRecordExcelDTO {

    @ExcelProperty("入院日期")
    private LocalDate enterDate;

    @ExcelProperty("出院日期")
    @ExcelIgnore
    private LocalDateTime outDate;

    @ExcelProperty("入院原因")
    private String enterReason;

    @ExcelProperty("床号")
    private String bedNumber;

    @ExcelProperty("患者姓名")
    private String patientName;

    @ExcelProperty("联系人姓名")
    private String contactName;

    @ExcelProperty("同患者关系")
    private String contactRelation;

    @ExcelProperty("联系人所在地区")
    private String contactArea;

    @ExcelProperty("联系人手机号1")
    private String contactPhone1;

    @ExcelProperty("联系人手机号2")
    private String contactPhone2;

    @ExcelProperty("状态")
    private InPatientStateEnum state;

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

    public String getState() {
        return state.getDescript();
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
