package com.i2i.optivolt.home.dto;
 
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
 
@Data
public class ApplianceDto {
    private String name;
    private String type;
 
    @JsonAlias("wattLimit")
    private Double safePowerLimit;
}
 