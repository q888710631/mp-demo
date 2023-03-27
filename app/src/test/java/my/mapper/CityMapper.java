package my.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import my.model.City;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CityMapper extends BaseMapper<City> {
}
