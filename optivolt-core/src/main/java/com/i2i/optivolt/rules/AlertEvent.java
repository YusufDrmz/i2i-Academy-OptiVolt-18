package com.i2i.optivolt.rules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AlertEvent {
    private Long homeId;
    private AlertType type;
    private Long applianceId;
    private String applianceName;
    private double totalWattToday;
    private double totalCostToday;
    private double budgetQuotaTry;

    @Builder.Default
    private Instant occurredAt = Instant.now();
}
