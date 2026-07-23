package com.i2i.optivolt.home.dto;

import lombok.Data;

@Data
public class ApplianceDto {
    private String name;
    private String type;
    private Double safePowerLimit;
}
