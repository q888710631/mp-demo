package com.honyee.app.mapperstruct;

import com.honyee.app.dto.ArticleDTO;
import com.honyee.app.mapperstruct.base.EntityMapper;
import com.honyee.app.model.Article;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticleMapperStruct extends EntityMapper<ArticleDTO, Article> {

}
