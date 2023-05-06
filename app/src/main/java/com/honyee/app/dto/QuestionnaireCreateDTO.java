package com.honyee.app.dto;

import com.honyee.app.config.Constants;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Pattern;

@Schema(title = "问卷")
public class QuestionnaireCreateDTO{
    @Schema(title = "姓名")
    private String fullName;

    @Schema(title = "手机号")
    @Pattern(regexp = Constants.REGEXP_PHONE_NUMBER, message = "手机号格式异常")
    private String phoneNumber;

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
