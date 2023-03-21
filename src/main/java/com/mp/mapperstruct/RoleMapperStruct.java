package com.mp.mapperstruct;

import com.mp.dto.RoleDTO;
import com.mp.mapperstruct.base.EntityMapper;
import com.mp.model.Role;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapperStruct extends EntityMapper<RoleDTO, Role> {

    default Collection<String> toRoleKey(Collection<Role> roles){
        if (roles == null) {
            return null;
        }
        return roles.stream().map(Role::getRoleKey).collect(Collectors.toList());
    }

    default Collection<Role> toRole(Collection<String> roles){
        if (roles == null) {
            return null;
        }
        return roles.stream().map(roleKey-> {
            Role role = new Role();
            role.setRoleKey(roleKey);
            return role;
        }).collect(Collectors.toList());
    }
}
