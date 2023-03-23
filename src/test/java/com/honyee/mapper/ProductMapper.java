package com.honyee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honyee.model.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
