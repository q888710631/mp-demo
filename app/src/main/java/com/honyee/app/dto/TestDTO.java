package com.honyee.app.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public class TestDTO implements Serializable {
    private String data = "123123";
    private BigDecimal bigDecimal = BigDecimal.valueOf(1234);
    /**
     * 输入输出格式：2021-05-09 12:26:26
     */
    private Instant instant = Instant.now();
    /**
     * 输入输出格式：2021-05-09 12:26:26
     */
    private LocalDateTime localDateTime = LocalDateTime.now();

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }
}
