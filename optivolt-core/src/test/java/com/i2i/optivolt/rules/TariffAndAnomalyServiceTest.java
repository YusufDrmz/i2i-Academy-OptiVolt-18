package com.i2i.optivolt.rules;

import com.i2i.optivolt.rules.model.ApplianceState;
import com.i2i.optivolt.rules.model.HomeState;
import com.i2i.optivolt.rules.model.TariffState;
import com.i2i.optivolt.rules.store.InMemoryHomeStateStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TariffAndAnomalyServiceTest {
    private InMemoryHomeStateStore store;
    private RecordingAlertPublisher alertPublisher;
    private TariffAndAnomalyService service;

    @BeforeEach
    void setUp() {
        store = new InMemoryHomeStateStore();
        alertPublisher = new RecordingAlertPublisher();
        service = new TariffAndAnomalyService(store, alertPublisher);

        HomeState home = HomeState.builder()
                .homeId(1L)
                .totalWattToday(0)
                .totalCostToday(0)
                .powerQuotaWatt(5000)
                .budgetQuotaTry(1000)
                .standardRate(2.5)
                .penaltyRate(5.0)
                .applianceStates(new HashMap<>())
                .build();
        home.getApplianceStates().put(10L, ApplianceState.builder()
                .applianceId(10L)
                .deviceName("Çamaşır Makinesi")
                .maxSafeWatt(2200)
                .build());
        store.save(home);
    }

    @Test
    void firesQuota80AlertOnce() {
        store.findByHomeId(1L).get().setTotalCostToday(850); // 85%
        service.evaluateQuota(1L);
        service.evaluateQuota(1L); // calling twice must not double-fire

        assertEquals(1, alertPublisher.events.size());
        assertEquals(AlertType.QUOTA_80_PERCENT, alertPublisher.events.get(0).getType());
    }

    @Test
    void breachingBudgetSwitchesToPenaltyTariff() {
        store.findByHomeId(1L).get().setTotalCostToday(1100); // 110%
        service.evaluateQuota(1L);

        HomeState updated = store.findByHomeId(1L).get();
        assertEquals(TariffState.PENALTY, updated.getTariffState());
        assertEquals(AlertType.QUOTA_100_PERCENT, alertPublisher.events.get(0).getType());
        assertEquals(5.0, service.currentRate(1L));
    }

    @Test
    void appliancesBecomeAnomalousAfterThreeConsecutiveBreaches() {
        setApplianceWatt(2500); // over 2200 limit
        service.evaluateApplianceBreach(1L, 10L);
        service.evaluateApplianceBreach(1L, 10L);
        assertTrue(alertPublisher.events.isEmpty());

        service.evaluateApplianceBreach(1L, 10L); // 3rd consecutive breach
        assertEquals(1, alertPublisher.events.size());
        assertEquals(AlertType.DEVICE_ANOMALY, alertPublisher.events.get(0).getType());
        assertTrue(store.findByHomeId(1L).get().getApplianceStates().get(10L).isAnomalous());
    }

    @Test
    void breachCounterResetsWhenBackToNormal() {
        setApplianceWatt(2500);
        service.evaluateApplianceBreach(1L, 10L);
        service.evaluateApplianceBreach(1L, 10L);

        setApplianceWatt(1000); // back under the safe limit
        service.evaluateApplianceBreach(1L, 10L);

        assertEquals(0, store.findByHomeId(1L).get().getApplianceStates().get(10L).getConsecutiveBreachCount());
        assertTrue(alertPublisher.events.isEmpty());
    }

    private void setApplianceWatt(double watt) {
        store.findByHomeId(1L).get().getApplianceStates().get(10L).setLastWatt(watt);
    }

    static class RecordingAlertPublisher implements AlertPublisher {
        final List<AlertEvent> events = new java.util.ArrayList<>();

        @Override
        public void publish(AlertEvent event) {
            events.add(event);
        }
    }
}
