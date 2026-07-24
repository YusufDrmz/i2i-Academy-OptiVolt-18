package com.i2i.telemetrysensors.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RegistrationMessage(
        @JsonProperty("id") Long homeId,
        List<ApplianceDefinition> appliances
) {
    public record ApplianceDefinition(
            @JsonProperty("id") Long applianceId,
            @JsonProperty("name") String deviceName,
            @JsonProperty("type") String deviceType,
            @JsonProperty("safePowerLimit") double maxSafeWatt
    ) {
    }
}