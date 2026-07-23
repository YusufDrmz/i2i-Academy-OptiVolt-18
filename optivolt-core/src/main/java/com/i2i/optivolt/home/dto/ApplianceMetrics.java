package com.i2i.optivolt.home.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ApplianceMetrics implements Serializable {
    private Long applianceId;
    private Double currentConsumption;
    private Integer consecutiveBreaches;
    private Boolean isAnomalous;
}
