package com.honyee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honyee.dto.RoleDTO;
import com.honyee.dto.UserRoleDTO;
import com.honyee.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select * from user_role t " +
        "inner join role r on r.id = t.role_id " +
        "where user_id = #{user_id}")
    List<Role> findRolesByUserId(@Param("user_id") Long userId);

    @Select("select role_key, role_name from role")
    List<RoleDTO> findRoleList();

    List<UserRoleDTO> findRoleKeyByUserIdIn(@Param("ids") List<Long> userIds);
}
