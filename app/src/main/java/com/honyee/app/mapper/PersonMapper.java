package com.honyee.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honyee.app.model.test.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PersonMapper extends BaseMapper<Person> {

    List<Person> findAll();

    List<Person> findLike(@Param("name") String name);


}
