package com.honyee.mapperstruct;

import com.honyee.dto.UserDTO;
import com.honyee.mapperstruct.base.EntityMapper;
import com.honyee.model.User;
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
