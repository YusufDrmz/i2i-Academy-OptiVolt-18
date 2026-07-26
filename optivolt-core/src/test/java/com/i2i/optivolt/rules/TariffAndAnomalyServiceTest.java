package com.i2i.optivolt.rules;

import com.i2i.optivolt.home.dto.ApplianceMetrics;
import com.i2i.optivolt.home.dto.HomeMetrics;
import com.i2i.optivolt.home.entity.Appliance;
import com.i2i.optivolt.home.entity.Home;
import com.i2i.optivolt.home.repository.ApplianceRepository;
import com.i2i.optivolt.home.repository.HomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TariffAndAnomalyServiceTest {

    private ApplianceRepository applianceRepository;
    private HomeRepository homeRepository;
    private RecordingAlertPublisher alertPublisher;
    private TariffAndAnomalyService service;
    private HomeMetrics homeMetrics;

    @BeforeEach
    void setUp() {
        applianceRepository = mock(ApplianceRepository.class);
        homeRepository = mock(HomeRepository.class);
        alertPublisher = new RecordingAlertPublisher();
        service = new TariffAndAnomalyService(applianceRepository, homeRepository, alertPublisher);

        Appliance washingMachine = Appliance.builder()
                .id(10L)
                .name("Çamaşır Makinesi")
                .type("WashingMachine")
                .safePowerLimit(2200.0)
                .build();
        when(applianceRepository.findById(10L)).thenReturn(Optional.of(washingMachine));

        Home testHome = Home.builder()
                .id(1L)
                .maxPowerBudget(5000.0)
                .maxFinancialBudget(100.0)
                .standardRate(2.5)
                .penaltyRate(5.0)
                .build();
        when(homeRepository.findById(1L)).thenReturn(Optional.of(testHome));

        homeMetrics = new HomeMetrics();
        homeMetrics.setHomeId(1L);
        homeMetrics.setCurrentTotalConsumption(0.0);
        homeMetrics.setCurrentBillingTotal(0.0);
        homeMetrics.setIsPenaltyTariffActive(false);
        homeMetrics.setApplianceMetrics(new HashMap<>());

        ApplianceMetrics appMetrics = new ApplianceMetrics();
        appMetrics.setApplianceId(10L);
        appMetrics.setCurrentConsumption(0.0);
        appMetrics.setConsecutiveBreaches(0);
        appMetrics.setIsAnomalous(false);
        homeMetrics.getApplianceMetrics().put(10L, appMetrics);
    }

    @Test
    void appliancesBecomeAnomalousAfterThreeConsecutiveBreaches() {
        setWatt(2500);
        service.evaluateApplianceBreach(homeMetrics, 10L);
        service.evaluateApplianceBreach(homeMetrics, 10L);
        assertTrue(alertPublisher.events.isEmpty());

        service.evaluateApplianceBreach(homeMetrics, 10L);
        assertEquals(1, alertPublisher.events.size());
        assertEquals(AlertType.DEVICE_ANOMALY, alertPublisher.events.get(0).getType());
        assertTrue(homeMetrics.getApplianceMetrics().get(10L).getIsAnomalous());
    }

    @Test
    void breachCounterResetsWhenBackToNormal() {
        setWatt(2500);
        service.evaluateApplianceBreach(homeMetrics, 10L);
        service.evaluateApplianceBreach(homeMetrics, 10L);

        setWatt(1000);
        service.evaluateApplianceBreach(homeMetrics, 10L);

        assertEquals(0, homeMetrics.getApplianceMetrics().get(10L).getConsecutiveBreaches());
        assertTrue(alertPublisher.events.isEmpty());
    }

    @Test
    void quotaWarningFiresOnceAtEightyPercentOfBudget() {
        homeMetrics.setCurrentBillingTotal(85.0);
        service.evaluateHomeQuota(homeMetrics, 1L);
        assertEquals(1, alertPublisher.events.stream().filter(e -> e.getType() == AlertType.QUOTA_80_PERCENT).count());

        service.evaluateHomeQuota(homeMetrics, 1L);
        assertEquals(1, alertPublisher.events.stream().filter(e -> e.getType() == AlertType.QUOTA_80_PERCENT).count());
    }

    @Test
    void quotaCriticalFiresAtHundredPercentOfBudget() {
        homeMetrics.setCurrentBillingTotal(120.0);
        service.evaluateHomeQuota(homeMetrics, 1L);
        assertEquals(1, alertPublisher.events.stream().filter(e -> e.getType() == AlertType.QUOTA_100_PERCENT).count());
    }

    @Test
    void penaltyTariffActivatesWhenOverPowerQuota() {
        homeMetrics.setCurrentTotalConsumption(6000.0); // over the 5000W quota
        service.evaluateHomeQuota(homeMetrics, 1L);
        assertTrue(homeMetrics.getIsPenaltyTariffActive());
    }

    private void setWatt(double watt) {
        homeMetrics.getApplianceMetrics().get(10L).setCurrentConsumption(watt);
    }

    static class RecordingAlertPublisher implements AlertPublisher {
        final List<AlertEvent> events = new java.util.ArrayList<>();

        @Override
        public void publish(AlertEvent event) {
            events.add(event);
        }
    }
}