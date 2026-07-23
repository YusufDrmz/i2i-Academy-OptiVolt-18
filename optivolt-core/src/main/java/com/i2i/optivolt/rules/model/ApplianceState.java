package com.i2i.optivolt.rules.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ApplianceState {
    private Long applianceId;
    private String deviceName;
    private double maxSafeWatt;
    private double lastWatt;
    private int consecutiveBreachCount;
    private boolean anomalous;
}
