package com.honyee.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honyee.app.model.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
