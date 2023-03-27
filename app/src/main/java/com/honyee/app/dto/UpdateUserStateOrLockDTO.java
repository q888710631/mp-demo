package com.honyee.app.dto;

import com.honyee.app.enums.UserStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(title = "用户DTO")
public class UpdateUserStateOrLockDTO {
    @NotNull
    @Schema(title = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(title = "解锁", description = "解锁时为true，锁定时为false，为空不处理", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean unlock;

    @Schema(title = "状态")
    private UserStateEnum state;

    @Schema(title = "锁定开始时间")
    private LocalDateTime lockBeginDate;

    @Schema(title = "锁定结束时间")
    private LocalDateTime lockEndDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getUnlock() {
        return unlock;
    }

    public void setUnlock(Boolean unlock) {
        this.unlock = unlock;
    }

    public UserStateEnum getState() {
        return state;
    }

    public void setState(UserStateEnum state) {
        this.state = state;
    }

    public LocalDateTime getLockBeginDate() {
        return lockBeginDate;
    }

    public void setLockBeginDate(LocalDateTime lockBeginDate) {
        this.lockBeginDate = lockBeginDate;
    }

    public LocalDateTime getLockEndDate() {
        return lockEndDate;
    }

    public void setLockEndDate(LocalDateTime lockEndDate) {
        this.lockEndDate = lockEndDate;
    }
}
