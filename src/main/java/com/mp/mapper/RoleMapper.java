package com.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mp.model.Role;
import com.mp.model.User;
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
}
