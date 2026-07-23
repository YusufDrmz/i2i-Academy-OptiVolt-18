package com.i2i.telemetrysensors.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SimulatedAppliance {
    private Long applianceId;
    private String deviceName;
    private String deviceType;
    private double maxSafeWatt;
}
