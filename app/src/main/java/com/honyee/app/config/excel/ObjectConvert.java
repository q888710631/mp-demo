package com.honyee.app.config.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;

/**
 * 用于EXCEL导出时，Object.toString()
 */
public class ObjectConvert implements Converter<Object> {

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Object> context) {
        Object value = context.getValue();
        WriteCellData<String> cellData = new WriteCellData<>();
        if (value != null) {
            cellData.setStringValue(value.toString());
        }
        cellData.setType(CellDataTypeEnum.STRING);
        return cellData;
    }

}
