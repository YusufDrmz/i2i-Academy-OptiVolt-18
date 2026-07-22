package com.i2i.optivolt.rules;

import com.i2i.optivolt.rules.model.ApplianceState;
import com.i2i.optivolt.rules.model.HomeState;
import com.i2i.optivolt.rules.model.TariffState;
import com.i2i.optivolt.rules.store.HomeStateStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor

public class TariffAndAnomalyService {
    private static final int ANOMALY_THRESHOLD_CYCLES = 3;

    private final HomeStateStore homeStateStore;
    private final AlertPublisher alertPublisher;

    public void evaluateQuota(Long homeId) {
        HomeState state = homeStateStore.findByHomeId(homeId)
                .orElseThrow(() -> new IllegalStateException("Unknown home: " + homeId));

        if (state.getBudgetQuotaTry() <= 0) {
            log.warn("Home {} has no budget quota configured, skipping evaluation", homeId);
            return;
        }

        double percentOfBudget = (state.getTotalCostToday() / state.getBudgetQuotaTry()) * 100.0;

        if (percentOfBudget >= 100.0 && !state.isQuota100Triggered()) {
            state.setQuota100Triggered(true);
            state.setTariffState(TariffState.PENALTY);
            homeStateStore.save(state);
            log.info("Home {} breached 100% budget quota - penalty tariff activated", homeId);
            alertPublisher.publish(AlertEvent.builder()
                    .homeId(homeId)
                    .type(AlertType.QUOTA_100_PERCENT)
                    .totalWattToday(state.getTotalWattToday())
                    .totalCostToday(state.getTotalCostToday())
                    .budgetQuotaTry(state.getBudgetQuotaTry())
                    .build());
        } else if (percentOfBudget >= 80.0 && !state.isQuota80Triggered()) {
            state.setQuota80Triggered(true);
            homeStateStore.save(state);
            log.info("Home {} reached 80% of budget quota", homeId);
            alertPublisher.publish(AlertEvent.builder()
                    .homeId(homeId)
                    .type(AlertType.QUOTA_80_PERCENT)
                    .totalWattToday(state.getTotalWattToday())
                    .totalCostToday(state.getTotalCostToday())
                    .budgetQuotaTry(state.getBudgetQuotaTry())
                    .build());
        }
    }

    public void evaluateApplianceBreach(Long homeId, Long applianceId) {
        HomeState state = homeStateStore.findByHomeId(homeId)
                .orElseThrow(() -> new IllegalStateException("Unknown home: " + homeId));

        ApplianceState appliance = state.getApplianceStates().get(applianceId);
        if (appliance == null) {
            log.warn("Unknown appliance {} for home {}, skipping anomaly check", applianceId, homeId);
            return;
        }

        boolean overLimit = appliance.getLastWatt() > appliance.getMaxSafeWatt();

        if (overLimit) {
            appliance.setConsecutiveBreachCount(appliance.getConsecutiveBreachCount() + 1);

            if (appliance.getConsecutiveBreachCount() >= ANOMALY_THRESHOLD_CYCLES && !appliance.isAnomalous()) {
                appliance.setAnomalous(true);
                homeStateStore.save(state);
                log.info("Appliance {} on home {} marked anomalous after {} consecutive breaches",
                        applianceId, homeId, appliance.getConsecutiveBreachCount());
                alertPublisher.publish(AlertEvent.builder()
                        .homeId(homeId)
                        .type(AlertType.DEVICE_ANOMALY)
                        .applianceId(applianceId)
                        .applianceName(appliance.getDeviceName())
                        .totalWattToday(state.getTotalWattToday())
                        .totalCostToday(state.getTotalCostToday())
                        .budgetQuotaTry(state.getBudgetQuotaTry())
                        .build());
                return;
            }
        } else {
            appliance.setConsecutiveBreachCount(0);
            appliance.setAnomalous(false);
        }

        homeStateStore.save(state);
    }

    public double currentRate(Long homeId) {
        return homeStateStore.findByHomeId(homeId)
                .map(s -> s.getTariffState() == TariffState.PENALTY ? s.getPenaltyRate() : s.getStandardRate())
                .orElse(0.0);
    }
}
