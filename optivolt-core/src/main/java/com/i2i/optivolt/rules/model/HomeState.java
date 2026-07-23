package com.i2i.optivolt.rules.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class HomeState {
    private Long homeId;
    private double totalWattToday;
    private double totalCostToday;
    private double powerQuotaWatt;
    private double budgetQuotaTry;
    private double standardRate;
    private double penaltyRate;

    @Builder.Default
    private TariffState tariffState = TariffState.NORMAL;

    private boolean quota80Triggered;
    private boolean quota100Triggered;

    @Builder.Default
    private Map<Long, ApplianceState> applianceStates = new ConcurrentHashMap<>();
}
