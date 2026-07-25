package com.i2i.optivolt.rules;

import com.i2i.optivolt.home.dto.ApplianceMetrics;
import com.i2i.optivolt.home.dto.HomeMetrics;
import com.i2i.optivolt.home.entity.Appliance;
import com.i2i.optivolt.home.repository.ApplianceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TariffAndAnomalyServiceTest {

    private ApplianceRepository applianceRepository;
    private RecordingAlertPublisher alertPublisher;
    private TariffAndAnomalyService service;
    private HomeMetrics homeMetrics;

    @BeforeEach
    void setUp() {
        applianceRepository = mock(ApplianceRepository.class);
        alertPublisher = new RecordingAlertPublisher();
        service = new TariffAndAnomalyService(applianceRepository, alertPublisher);

        Appliance washingMachine = Appliance.builder()
                .id(10L)
                .name("Çamaşır Makinesi")
                .type("WashingMachine")
                .safePowerLimit(2200.0)
                .build();
        when(applianceRepository.findById(10L)).thenReturn(Optional.of(washingMachine));

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