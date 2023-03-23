package com.honyee.mapperstruct;

import com.honyee.dto.UserDTO;
import com.honyee.mapperstruct.base.EntityMapper;
import com.honyee.model.User;
import org.mapstruct.Mapper;

/**
 * toDto 需要用到RoleMapperStruct，通过 uses指定
 */
@Mapper(componentModel = "spring", uses = {RoleMapperStruct.class})
public interface UserMapperStruct extends EntityMapper<UserDTO, User> {
    UserDTO toDto(User user);

}
