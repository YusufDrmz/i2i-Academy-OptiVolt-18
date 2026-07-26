package com.i2i.optivolt.home.service;
 
import com.i2i.optivolt.home.dto.ApplianceMetrics;
import com.i2i.optivolt.home.dto.ApplianceStatusResponse;
import com.i2i.optivolt.home.dto.HomeDetailResponse;
import com.i2i.optivolt.home.dto.HomeMetrics;
import com.i2i.optivolt.home.dto.HomeSummaryResponse;
import com.i2i.optivolt.home.entity.Home;
import com.i2i.optivolt.home.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
 
/**
 * Combines Postgres (Home/Appliance) with the live Ignite cache (HomeMetrics)
 * into the flat shape the React dashboard expects.
 */
@Service
@RequiredArgsConstructor
public class HomeSummaryService {
 
    private final HomeRepository homeRepository;
    private final HomeStatusService homeStatusService;
 
    public List<HomeSummaryResponse> getAllSummaries() {
        return homeRepository.findAll().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }
 
    public HomeDetailResponse getDetail(Long homeId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new NoSuchElementException("Home not found: " + homeId));
        HomeMetrics metrics = homeStatusService.getHomeStatus(homeId);
 
        double currentWatt = metrics != null && metrics.getCurrentTotalConsumption() != null
                ? metrics.getCurrentTotalConsumption() : 0.0;
        double currentCost = metrics != null && metrics.getCurrentBillingTotal() != null
                ? metrics.getCurrentBillingTotal() : 0.0;
        double powerQuota = home.getMaxPowerBudget() != null ? home.getMaxPowerBudget() : 0.0;
        double budgetQuota = home.getMaxFinancialBudget() != null ? home.getMaxFinancialBudget() : 0.0;
 
        List<ApplianceStatusResponse> applianceResponses = home.getAppliances() == null
                ? List.of()
                : home.getAppliances().stream().map(a -> {
                    ApplianceMetrics am = metrics != null && metrics.getApplianceMetrics() != null
                            ? metrics.getApplianceMetrics().get(a.getId()) : null;
                    return ApplianceStatusResponse.builder()
                            .id(a.getId())
                            .name(a.getName())
                            .currentWatt(am != null && am.getCurrentConsumption() != null ? am.getCurrentConsumption() : 0.0)
                            .maxSafeWatt(a.getSafePowerLimit() != null ? a.getSafePowerLimit() : 0.0)
                            .isAnomalous(am != null && Boolean.TRUE.equals(am.getIsAnomalous()))
                            .consecutiveBreaches(am != null && am.getConsecutiveBreaches() != null ? am.getConsecutiveBreaches() : 0)
                            .build();
                }).collect(Collectors.toList());
 
        return HomeDetailResponse.builder()
                .id(home.getId())
                .name(home.getName() != null ? home.getName() : "Home #" + home.getId())
                .address(home.getAddress())
                .contactEmail(home.getContactEmail())
                .currentWatt(currentWatt)
                .powerQuotaWatt(powerQuota)
                .budgetQuotaTry(budgetQuota)
                .currentCostTry(currentCost)
                .status(computeStatus(currentWatt, powerQuota, currentCost, budgetQuota))
                .appliances(applianceResponses)
                .build();
    }
 
    private HomeSummaryResponse toSummary(Home home) {
        HomeMetrics metrics = homeStatusService.getHomeStatus(home.getId());
 
        double currentWatt = metrics != null && metrics.getCurrentTotalConsumption() != null
                ? metrics.getCurrentTotalConsumption() : 0.0;
        double currentCost = metrics != null && metrics.getCurrentBillingTotal() != null
                ? metrics.getCurrentBillingTotal() : 0.0;
        double powerQuota = home.getMaxPowerBudget() != null ? home.getMaxPowerBudget() : 0.0;
        double budgetQuota = home.getMaxFinancialBudget() != null ? home.getMaxFinancialBudget() : 0.0;
 
        return HomeSummaryResponse.builder()
                .id(home.getId())
                .name(home.getName() != null ? home.getName() : "Home #" + home.getId())
                .address(home.getAddress())
                .contactEmail(home.getContactEmail())
                .currentWatt(currentWatt)
                .powerQuotaWatt(powerQuota)
                .budgetQuotaTry(budgetQuota)
                .currentCostTry(currentCost)
                .status(computeStatus(currentWatt, powerQuota, currentCost, budgetQuota))
                .appliancesCount(home.getAppliances() == null ? 0 : home.getAppliances().size())
                .build();
    }
 
    private String computeStatus(double currentWatt, double powerQuota, double currentCost, double budgetQuota) {
        double powerPct = powerQuota > 0 ? (currentWatt / powerQuota) * 100 : 0;
        double budgetPct = budgetQuota > 0 ? (currentCost / budgetQuota) * 100 : 0;
        double worst = Math.max(powerPct, budgetPct);
        if (worst >= 100) return "CRITICAL";
        if (worst >= 80) return "WARNING";
        return "NORMAL";
    }
}
