package com.honyee.app.model;

import com.baomidou.mybatisplus.annotation.*;
import com.honyee.app.model.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "文章")
@TableName("article")
@InterceptorIgnore(tenantLine = "true")
public class Article extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(title = "标题")
    @TableField("title")
    private String title;

    @Schema(title = "封面")
    @TableField("cover")
    private String cover;

    @Schema(title = "内容")
    @TableField("content")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
