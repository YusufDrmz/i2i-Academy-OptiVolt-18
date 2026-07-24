package com.i2i.telemetrysensors.dto;

public record TelemetryMessage(
        Long homeId,
        Long applianceId,
        double currentWattage,
        long timestamp
) {
}