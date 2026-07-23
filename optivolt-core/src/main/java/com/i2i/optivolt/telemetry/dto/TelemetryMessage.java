package com.i2i.optivolt.telemetry.dto;

import lombok.Data;

@Data
public class TelemetryMessage {
    private Long homeId;
    private Long applianceId;
    private Double currentWattage;
    private Long timestamp;
}
