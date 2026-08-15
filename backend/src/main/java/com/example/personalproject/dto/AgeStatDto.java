package com.example.personalproject.dto;

import java.math.BigDecimal;

/** 問卷填答者的年齡區間統計，不回傳個別年齡。 */
public class AgeStatDto {

    private final String label;
    private final long count;
    private final BigDecimal percentage;

    public AgeStatDto(String label, long count, BigDecimal percentage) {
        this.label = label;
        this.count = count;
        this.percentage = percentage;
    }

    public String getLabel() { return label; }
    public long getCount() { return count; }
    public BigDecimal getPercentage() { return percentage; }
}
