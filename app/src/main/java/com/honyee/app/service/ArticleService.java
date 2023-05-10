package com.honyee.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.honyee.app.config.oss.OssUtil;
import com.honyee.app.dto.ArticleDTO;
import com.honyee.app.dto.base.PageResultDTO;
import com.honyee.app.dto.base.MyPage;
import com.honyee.app.exp.DataNotExistsException;
import com.honyee.app.mapper.ArticleMapper;
import com.honyee.app.mapperstruct.ArticleMapperStruct;
import com.honyee.app.model.Article;
import com.honyee.app.service.base.MyService;
import com.honyee.app.utils.SubUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService extends MyService<ArticleMapper, Article> {

    @Autowired
    private ArticleMapperStruct articleMapperStruct;

    @Autowired
    private OssUtil ossUtil;

    /**
     * 文章内容截取长度
     */
    private static final int CONTENT_LENGTH = 37;
    /**
     * 文章缩略后置内容
     */
    private static final String CONTENT_SUFFIX = "...";

    public PageResultDTO<ArticleDTO> findChainDTO(Long chainId, MyPage myPage) {
        IPage<Article> pageResult = lambdaQuery().gt(Article::getId, chainId).page(toPage(myPage));
        List<ArticleDTO> dtoList = articleMapperStruct.toDto(pageResult.getRecords());
        for (ArticleDTO articleDTO : dtoList) {
            String content = articleDTO.getContent();
            if (StringUtils.isNotBlank(content) && content.length() > CONTENT_LENGTH) {
                articleDTO.setContent(SubUtil.subString(content, 0, CONTENT_LENGTH) + CONTENT_SUFFIX);
            }
            articleDTO.setCover(ossUtil.getUrl(articleDTO.getCover()));
        }
        return PageResultDTO.build(dtoList, pageResult);
    }

    public ArticleDTO findDetailDTO(Long id) {
        Article article = lambdaQuery().eq(Article::getId, id).one();
        ArticleDTO articleDTO = articleMapperStruct.toDto(article);
        articleDTO.setCover(ossUtil.getUrl(articleDTO.getCover()));
        return articleDTO;
    }

    public void create(ArticleDTO dto) {
        Article entity = articleMapperStruct.toEntity(dto);
        entity.setId(null);
        save(entity);
    }

    public void modify(ArticleDTO dto) {
        Article entity = lambdaQuery().eq(Article::getId, dto.getId()).one();
        if (entity == null) {
            throw new DataNotExistsException(dto.getId());
        }
        entity.setTitle(dto.getTitle());
        entity.setCover(dto.getCover());
        entity.setContent(dto.getContent());
        updateById(entity);
    }

}
