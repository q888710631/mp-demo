package com.honyee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honyee.dto.UserDTO;
import com.honyee.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("select id, nickname, username from user")
    List<UserDTO> findUserDTOList();


}
