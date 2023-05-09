package com.honyee.app.dto.file;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传图片到oss结果
 */
public class UploadImageResultDTO {

    @Schema(name = "oss url ，当失败时，值为error")
    @NotNull
    private List<String> values;

    public UploadImageResultDTO() {
        values = new ArrayList<>();
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public UploadImageResultDTO(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "UploadImageBase64OrUrlResultDTO{" + "values=" + values + '}';
    }
}
