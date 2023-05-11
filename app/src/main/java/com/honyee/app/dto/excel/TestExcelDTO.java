package com.honyee.app.dto.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;

@ColumnWidth(33)
@HeadRowHeight(20)
@HeadFontStyle(fontHeightInPoints = 12, color = 0)
@HeadStyle(fillForegroundColor = 22, fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND)
@ContentFontStyle(fontHeightInPoints = 12)
@ContentStyle(
    borderLeft = BorderStyleEnum.THIN,
    borderRight = BorderStyleEnum.THIN,
    borderBottom = BorderStyleEnum.THIN,
    borderTop = BorderStyleEnum.THIN
)
public class TestExcelDTO {
    public static final WriteSheet SHEET = EasyExcel.writerSheet("业务数据").head(TestExcelDTO.class).build();

    @ExcelProperty("内容1")
    private String content1;

    @ExcelProperty("内容2")
    private String content2;

    public String getContent1() {
        return content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }
}
