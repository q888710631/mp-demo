package com.honyee.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honyee.app.model.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    Integer findUserTopRoleLevel(Long userId);
}
