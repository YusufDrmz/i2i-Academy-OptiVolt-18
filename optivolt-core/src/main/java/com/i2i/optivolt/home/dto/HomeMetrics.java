package com.i2i.optivolt.home.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

@Data
public class HomeMetrics implements Serializable {
    private Long homeId;
    private Double currentTotalConsumption;
    private Double currentBillingTotal;
    private Boolean isPenaltyTariffActive;
    private Map<Long, ApplianceMetrics> applianceMetrics;
}
