package com.i2i.optivolt.rules;
 
import com.i2i.optivolt.home.dto.ApplianceMetrics;
import com.i2i.optivolt.home.dto.HomeMetrics;
import com.i2i.optivolt.home.entity.Appliance;
import com.i2i.optivolt.home.entity.Home;
import com.i2i.optivolt.home.repository.ApplianceRepository;
import com.i2i.optivolt.home.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
 
 
@Slf4j
@Service
@RequiredArgsConstructor
public class TariffAndAnomalyService {
 
    private static final int ANOMALY_THRESHOLD_CYCLES = 3;
    private static final double DEFAULT_STANDARD_RATE = 2.5;
    private static final double DEFAULT_PENALTY_RATE = 5.0;
 
    private final ApplianceRepository applianceRepository;
    private final HomeRepository homeRepository;
    private final AlertPublisher alertPublisher;
 
    @Value("${voltwise.simulation.interval-ms:4000}")
    private long tickIntervalMs;
 
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
 
    public void evaluateHomeQuota(HomeMetrics metrics, Long homeId) {
        Home home = homeRepository.findById(homeId).orElse(null);
        if (home == null) {
            log.warn("No home found for id {}, skipping quota evaluation", homeId);
            return;
        }
 
        double totalWatt = metrics.getCurrentTotalConsumption() != null ? metrics.getCurrentTotalConsumption() : 0.0;
        double powerQuota = home.getMaxPowerBudget() != null ? home.getMaxPowerBudget() : 0.0;
        double budgetQuota = home.getMaxFinancialBudget() != null ? home.getMaxFinancialBudget() : 0.0;
        double standardRate = home.getStandardRate() != null ? home.getStandardRate() : DEFAULT_STANDARD_RATE;
        double penaltyRate = home.getPenaltyRate() != null ? home.getPenaltyRate() : DEFAULT_PENALTY_RATE;
 
        boolean overPowerQuota = powerQuota > 0 && totalWatt > powerQuota;
        metrics.setIsPenaltyTariffActive(overPowerQuota);
 
        double rate = overPowerQuota ? penaltyRate : standardRate;
        double incrementKwh = totalWatt * (tickIntervalMs / 3_600_000.0) / 1000.0;
        double incrementCost = incrementKwh * rate;
 
        double runningCost = metrics.getCurrentBillingTotal() != null ? metrics.getCurrentBillingTotal() : 0.0;
        runningCost += incrementCost;
        metrics.setCurrentBillingTotal(runningCost);
 
        double budgetPct = budgetQuota > 0 ? (runningCost / budgetQuota) * 100 : 0;
        boolean already80 = Boolean.TRUE.equals(metrics.getQuota80Notified());
        boolean already100 = Boolean.TRUE.equals(metrics.getQuota100Notified());
 
        if (budgetPct >= 100 && !already100) {
            metrics.setQuota100Notified(true);
            log.info("Home {} crossed 100% of budget quota ({} / {})", homeId, runningCost, budgetQuota);
            alertPublisher.publish(AlertEvent.builder()
                    .homeId(homeId)
                    .type(AlertType.QUOTA_100_PERCENT)
                    .totalWattToday(totalWatt)
                    .totalCostToday(runningCost)
                    .budgetQuotaTry(budgetQuota)
                    .build());
        } else if (budgetPct >= 80 && !already80) {
            metrics.setQuota80Notified(true);
            log.info("Home {} crossed 80% of budget quota ({} / {})", homeId, runningCost, budgetQuota);
            alertPublisher.publish(AlertEvent.builder()
                    .homeId(homeId)
                    .type(AlertType.QUOTA_80_PERCENT)
                    .totalWattToday(totalWatt)
                    .totalCostToday(runningCost)
                    .budgetQuotaTry(budgetQuota)
                    .build());
        }
    }
}
