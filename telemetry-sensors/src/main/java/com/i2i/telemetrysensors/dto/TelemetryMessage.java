package com.i2i.telemetrysensors.dto;

import java.time.Instant;

public record TelemetryMessage(
    Long homeId,
    Long applianceId,
    double watt,
    Instant timestamp
) {
}