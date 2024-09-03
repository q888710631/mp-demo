package com.honyee.app.dto.file;

import java.util.List;

public class UploadJsonOrGifResultDTO {
    Boolean result;
    List<FileResDTO> list;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public List<FileResDTO> getList() {
        return list;
    }

    public void setList(List<FileResDTO> list) {
        this.list = list;
    }
}
