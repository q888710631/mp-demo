package com.honyee.app.mapperstruct;

import com.honyee.app.dto.UserDTO;
import com.honyee.app.mapperstruct.base.EntityMapper;
import com.honyee.app.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * toDto 需要用到RoleMapperStruct，通过 uses指定
 */
@Mapper(componentModel = "spring", uses = {RoleMapperStruct.class})
public interface UserMapperStruct extends EntityMapper<UserDTO, User> {

    @Override
    @Mapping(source = "password", target = "password", ignore = true)
    UserDTO toDto(User user);

}
