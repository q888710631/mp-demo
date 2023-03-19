package com.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mp.dto.UserDTO;
import com.mp.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("select id, nickname, username from user")
    List<UserDTO> findUserDTOList();


}
