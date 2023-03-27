package com.honyee.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honyee.app.model.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    @Delete("delete from user_role where user_id = #{user_id} and role_id = #{role_id}")
    int deleteByUserIdAndRoleId(Long userId, Long roleId);
}
