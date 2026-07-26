package com.i2i.optivolt.telemetry.service;
 
import com.i2i.optivolt.config.IgniteConfig;
import com.i2i.optivolt.home.dto.ApplianceMetrics;
import com.i2i.optivolt.home.dto.HomeMetrics;
import com.i2i.optivolt.rules.TariffAndAnomalyService;
import com.i2i.optivolt.telemetry.dto.TelemetryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Service;
 
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
 
@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryProcessingService {
 
    private final Ignite ignite;
    private final TariffAndAnomalyService tariffAndAnomalyService;
    private IgniteCache<String, HomeMetrics> homeMetricsCache;
 
    @PostConstruct
    public void init() {
        this.homeMetricsCache = ignite.cache(IgniteConfig.HOME_METRICS_CACHE);
    }
 
    public void processTelemetry(TelemetryMessage message) {
        String cacheKey = String.valueOf(message.getHomeId());
 
        HomeMetrics metrics = homeMetricsCache.get(cacheKey);
        if (metrics == null) {
            metrics = new HomeMetrics();
            metrics.setHomeId(message.getHomeId());
            metrics.setCurrentTotalConsumption(0.0);
            metrics.setCurrentBillingTotal(0.0);
            metrics.setIsPenaltyTariffActive(false);
            metrics.setApplianceMetrics(new HashMap<>());
        }
 
        ApplianceMetrics appMetrics = metrics.getApplianceMetrics().getOrDefault(message.getApplianceId(), new ApplianceMetrics());
        appMetrics.setApplianceId(message.getApplianceId());
        appMetrics.setCurrentConsumption(message.getCurrentWattage());
 
        metrics.getApplianceMetrics().put(message.getApplianceId(), appMetrics);
 
        double total = metrics.getApplianceMetrics().values().stream()
                .mapToDouble(ApplianceMetrics::getCurrentConsumption)
                .sum();
        metrics.setCurrentTotalConsumption(total);
 
        tariffAndAnomalyService.evaluateApplianceBreach(metrics, message.getApplianceId());
        tariffAndAnomalyService.evaluateHomeQuota(metrics, message.getHomeId());
 
        homeMetricsCache.put(cacheKey, metrics);
        log.debug("Updated home metrics in Ignite for home ID: {}", message.getHomeId());
    }
}