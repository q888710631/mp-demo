package com.honyee.app.dto;

import com.honyee.app.enums.InPatientStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "住院记录-查询")
public class InpatientRecordUpdateQueryDTO {

    @Schema(title = "状态")
    private InPatientStateEnum state;

    public InPatientStateEnum getState() {
        return state;
    }

    public void setState(InPatientStateEnum state) {
        this.state = state;
    }
}
