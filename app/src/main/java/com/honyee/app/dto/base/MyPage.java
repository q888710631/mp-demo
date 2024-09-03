package com.honyee.app.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;

public class MyPage {
    @Schema(description = "页码，默认1，链式查询时无需传")
    private Long page;

    @Schema(description = "页长，默认10")
    private Long size;

    public Long getPage() {
        return page == null ? 1 : page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getSize() {
        return size == null ? 10 : size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
