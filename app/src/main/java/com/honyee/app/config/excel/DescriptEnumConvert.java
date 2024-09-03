package com.honyee.app.config.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;

public class DescriptEnumConvert implements Converter<DescriptEnum> {
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<DescriptEnum> context) throws Exception {
        DescriptEnum value = context.getValue();
        WriteCellData<?> cellData = new WriteCellData<>();
        cellData.setStringValue(value.getDescript());
        cellData.setType(CellDataTypeEnum.STRING);
        return cellData;
    }

}
