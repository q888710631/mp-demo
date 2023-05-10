package com.honyee.app.web;

import com.honyee.app.config.http.MyResponse;
import com.honyee.app.config.limit.RateLimit;
import com.honyee.app.dto.*;
import com.honyee.app.dto.base.PageResultDTO;
import com.honyee.app.dto.base.Insert;
import com.honyee.app.dto.base.MyPage;
import com.honyee.app.service.InpatientRecordService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inpatient-record")
public class InpatientRecordController {

    @Autowired
    private InpatientRecordService inpatientRecordService;

    @Operation(summary = "新增")
    @PostMapping("create")
    @RateLimit(mode = RateLimit.LimitMode.LOCK, lockKey = "#dto.patientName")
    public Object create(@Validated(Insert.class) @RequestBody InpatientRecordCreateDTO dto) {
        inpatientRecordService.create(dto);
        return MyResponse.ok();
    }

    @Operation(summary = "修改状态")
    @PostMapping("update-state")
    @RateLimit(mode = RateLimit.LimitMode.LOCK, lockKey = "#dto.id")
    public Object updateState(@Validated(Insert.class) @RequestBody InpatientRecordUpdateStateDTO dto) {
        inpatientRecordService.updateState(dto);
        return MyResponse.ok();
    }

    @Operation(summary = "住院记录列表")
    @GetMapping("/page")
    public PageResultDTO<InpatientRecordDTO> findPage(@RequestParam InpatientRecordUpdateQueryDTO queryDTO, MyPage myPage) {
        return inpatientRecordService.findPage(queryDTO, myPage);
    }

}
