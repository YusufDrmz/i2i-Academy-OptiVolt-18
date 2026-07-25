package com.i2i.optivolt.rules;

import com.i2i.optivolt.home.dto.ApplianceMetrics;
import com.i2i.optivolt.home.dto.HomeMetrics;
import com.i2i.optivolt.home.entity.Appliance;
import com.i2i.optivolt.home.repository.ApplianceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class TariffAndAnomalyService {

    private static final int ANOMALY_THRESHOLD_CYCLES = 3;

    private final ApplianceRepository applianceRepository;
    private final AlertPublisher alertPublisher;

    public void evaluateApplianceBreach(HomeMetrics metrics, Long applianceId) {
        ApplianceMetrics appMetrics = metrics.getApplianceMetrics().get(applianceId);
        if (appMetrics == null) {
            log.warn("No appliance metrics found for appliance {} on home {}", applianceId, metrics.getHomeId());
            return;
        }

        Appliance appliance = applianceRepository.findById(applianceId).orElse(null);
        if (appliance == null || appliance.getSafePowerLimit() == null) {
            log.warn("No safe power limit configured for appliance {}, skipping anomaly check", applianceId);
            return;
        }

        double currentWatt = appMetrics.getCurrentConsumption() != null ? appMetrics.getCurrentConsumption() : 0.0;
        boolean overLimit = currentWatt > appliance.getSafePowerLimit();

        int breaches = appMetrics.getConsecutiveBreaches() != null ? appMetrics.getConsecutiveBreaches() : 0;
        boolean wasAnomalous = Boolean.TRUE.equals(appMetrics.getIsAnomalous());

        if (overLimit) {
            breaches++;
            appMetrics.setConsecutiveBreaches(breaches);

            if (breaches >= ANOMALY_THRESHOLD_CYCLES && !wasAnomalous) {
                appMetrics.setIsAnomalous(true);
                log.info("Appliance {} on home {} marked anomalous after {} consecutive breaches",
                        applianceId, metrics.getHomeId(), breaches);
                alertPublisher.publish(AlertEvent.builder()
                        .homeId(metrics.getHomeId())
                        .type(AlertType.DEVICE_ANOMALY)
                        .applianceId(applianceId)
                        .applianceName(appliance.getName())
                        .totalWattToday(metrics.getCurrentTotalConsumption() != null ? metrics.getCurrentTotalConsumption() : 0.0)
                        .totalCostToday(metrics.getCurrentBillingTotal() != null ? metrics.getCurrentBillingTotal() : 0.0)
                        .budgetQuotaTry(0.0)
                        .build());
            }
        } else {
            appMetrics.setConsecutiveBreaches(0);
            appMetrics.setIsAnomalous(false);
        }
    }
}