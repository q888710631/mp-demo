package com.honyee.app.dto;

import com.honyee.app.dto.base.Update;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(title = "文章")
public class ArticleDTO {
    @NotNull(groups = Update.class, message = "id不能为空")
    private Long id;

    @Schema(title = "标题")
    @NotNull(message = "标题不能为空")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(title = "封面")
    @NotNull(message = "封面不能为空")
    @NotBlank(message = "封面不能为空")
    private String cover;

    @Schema(title = "内容")
    @NotNull(message = "内容不能为空")
    @NotBlank(message = "内容不能为空")
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
