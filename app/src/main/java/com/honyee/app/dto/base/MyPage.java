package com.honyee.app.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;

public class MyPage {
    @Schema(description = "页码，默认1，链式查询时无需传")
    private long page = 1;

    @Schema(description = "页长，默认10")
    private long size = 10;

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
