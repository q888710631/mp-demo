package com.honyee.app.dto.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 分页查询结果
 */
public class PageResultDTO<T> {
    @Schema(title = "数据")
    private List<T> list;

    @Schema(title = "总数量")
    private long total;

    @Schema(title = "总页数")
    private long pages;

    @Schema(title = "当前页码")
    private long current;

    @Schema(title = "页长")
    private long size;

    public static <T> PageResultDTO<T> build(List<T> list, IPage<?> iPage) {
        PageResultDTO<T> pageResultDTO = new PageResultDTO<>();
        pageResultDTO.total = iPage.getTotal();
        pageResultDTO.pages = iPage.getPages();
        pageResultDTO.current = iPage.getCurrent();
        pageResultDTO.size = iPage.getSize();
        pageResultDTO.list = list;
        return pageResultDTO;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
