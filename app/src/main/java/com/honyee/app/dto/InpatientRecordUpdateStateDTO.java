package com.honyee.app.dto;

import com.honyee.app.enums.InPatientStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Schema(title = "住院记录-更新状态")
public class InpatientRecordUpdateStateDTO {
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(title = "状态")
    @NotNull(message = "状态不能为空")
    private InPatientStateEnum state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InPatientStateEnum getState() {
        return state;
    }

    public void setState(InPatientStateEnum state) {
        this.state = state;
    }
}
