package com.honyee.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.honyee.app.dto.InpatientRecordCreateDTO;
import com.honyee.app.dto.InpatientRecordDTO;
import com.honyee.app.dto.InpatientRecordUpdateQueryDTO;
import com.honyee.app.dto.InpatientRecordUpdateStateDTO;
import com.honyee.app.dto.base.MyPage;
import com.honyee.app.dto.base.PageResultDTO;
import com.honyee.app.enums.InPatientStateEnum;
import com.honyee.app.exp.CommonException;
import com.honyee.app.exp.DataNotExistsException;
import com.honyee.app.mapper.InpatientRecordMapper;
import com.honyee.app.mapperstruct.InpatientRecordMapperStruct;
import com.honyee.app.model.InpatientRecord;
import com.honyee.app.service.base.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InpatientRecordService extends MyService<InpatientRecordMapper, InpatientRecord> {

    @Autowired
    private InpatientRecordMapperStruct inpatientRecordMapperStruct;

    public void create(InpatientRecordCreateDTO dto) {
        InpatientRecord entity = inpatientRecordMapperStruct.toEntity(dto);
        entity.setState(InPatientStateEnum.IN);
        save(entity);
    }

    public void updateState(InpatientRecordUpdateStateDTO dto) {
        InpatientRecord entity = lambdaQuery().eq(InpatientRecord::getId, dto.getId()).one();
        if (entity == null) {
            throw new DataNotExistsException(dto.getId());
        }
        InPatientStateEnum state = dto.getState();
        if (dto.getState() == InPatientStateEnum.ALL) {
            throw new CommonException("状态不正确");
        }
        entity.setState(dto.getState());
        if (state == InPatientStateEnum.OUT) {
            entity.setOutDate(LocalDateTime.now());
        }
        updateById(entity);
    }

    public PageResultDTO<InpatientRecordDTO> findPage(InpatientRecordUpdateQueryDTO queryDTO, MyPage myPage) {
        LambdaQueryChainWrapper<InpatientRecord> query = lambdaQuery();
        InPatientStateEnum state = queryDTO.getState();
        if (state != null && state != InPatientStateEnum.ALL) {
            query.eq(InpatientRecord::getState, state);
        }
        IPage<InpatientRecord> pageResult = query.page(toPage(myPage));
        List<InpatientRecordDTO> dtoList = inpatientRecordMapperStruct.toDto(pageResult.getRecords());
        return PageResultDTO.build(dtoList, pageResult);
    }
}