package com.honyee.app.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 上传图片到oss DTO
 *
 * @author wu.dunhong
 */
public class UploadImageDTO {

    @Schema(name = "base64 或者 图片的url")
    @NotNull
    private List<String> values;

    public UploadImageDTO() {
        values = new ArrayList<>();
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return (
            "UploadImageBase64OrUrlDTO{" +
                "values=" +
                values.stream().map(v -> v.length() > 100 ? "length=[" + v.length() + "" : v).collect(Collectors.joining(",")) +
                "]" +
                '}'
        );
    }
}
