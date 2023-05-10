package com.honyee.app.mapperstruct;

import com.honyee.app.dto.InpatientRecordCreateDTO;
import com.honyee.app.dto.InpatientRecordDTO;
import com.honyee.app.mapperstruct.base.EntityMapper;
import com.honyee.app.model.InpatientRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InpatientRecordMapperStruct extends EntityMapper<InpatientRecordDTO, InpatientRecord> {
    InpatientRecord toEntity(InpatientRecordCreateDTO ddto);
}
