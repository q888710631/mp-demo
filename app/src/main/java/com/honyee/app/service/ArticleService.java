package com.honyee.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.honyee.app.config.oss.OssUtil;
import com.honyee.app.dto.ArticleDTO;
import com.honyee.app.dto.base.ChainDTO;
import com.honyee.app.dto.base.MyPage;
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

    public ChainDTO<ArticleDTO> findChainDTO(Long chainId, MyPage myPage) {
        IPage<Article> articleIPage = lambdaQuery().gt(Article::getId, chainId).page(toPage(myPage));
        List<ArticleDTO> dtoList = articleMapperStruct.toDto(articleIPage.getRecords());
        for (ArticleDTO articleDTO : dtoList) {
            String content = articleDTO.getContent();
            if (StringUtils.isNotBlank(content) && content.length() > CONTENT_LENGTH) {
                articleDTO.setContent(SubUtil.subString(content, 0, CONTENT_LENGTH) + CONTENT_SUFFIX);
            }
            articleDTO.setCover(ossUtil.getUrl(articleDTO.getCover()));
        }
        return ChainDTO.build(dtoList, articleIPage);
    }

    public ArticleDTO findDetailDTO(Long id) {
        Article article = lambdaQuery().eq(Article::getId, id).one();
        ArticleDTO articleDTO = articleMapperStruct.toDto(article);
        articleDTO.setCover(ossUtil.getUrl(articleDTO.getCover()));
        return articleDTO;
    }

    public void create(ArticleDTO dto) {
        Article article = articleMapperStruct.toEntity(dto);
        article.setId(null);
        save(article);
    }

    public void modify(ArticleDTO dto) {
        Article article = lambdaQuery().eq(Article::getId, dto.getId()).one();
        article.setTitle(dto.getTitle());
        article.setCover(dto.getCover());
        article.setContent(dto.getContent());
        updateById(article);
    }

}
