package com.i2i.telemetrysensors.dto;

import java.util.List;

public record RegistrationMessage(Long homeId, List<ApplianceDefinition> appliances) {

    public record ApplianceDefinition(
            Long applianceId,
            String deviceName,
            String deviceType,
            double maxSafeWatt
    ) {
    }
}