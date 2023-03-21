package com.mp.mapperstruct;

import com.mp.dto.UserDTO;
import com.mp.mapperstruct.base.EntityMapper;
import com.mp.model.User;
import org.mapstruct.Mapper;

/**
 * toDto 需要用到RoleMapperStruct，通过 uses指定
 */
@Mapper(componentModel = "spring", uses = {RoleMapperStruct.class})
public interface UserMapperStruct extends EntityMapper<UserDTO, User> {
    UserDTO toDto(User user);

}
