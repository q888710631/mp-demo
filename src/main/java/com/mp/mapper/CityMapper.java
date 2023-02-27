package com.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mp.dto.CitySimpleDTO;
import com.mp.model.City;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CityMapper extends BaseMapper<City> {
    List<CitySimpleDTO> findSimple(City city);
}
