package com.i2i.telemetrysensors.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i2i.telemetrysensors.dto.TelemetryMessage;
import com.i2i.telemetrysensors.model.SimulatedAppliance;
import com.i2i.telemetrysensors.model.SimulatedHome;
import com.i2i.telemetrysensors.registry.SimulationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor

public class TelemetryPublisher {
    private final SimulationRegistry registry;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${voltwise.kafka.topic.telemetry:telemetry-stream}")
    private String telemetryTopic;

    private static final double ANOMALY_CHANCE = 0.05;

    @Scheduled(fixedDelayString = "${voltwise.simulation.interval-ms:4000}")
    public void publishTelemetryForAllHomes() {
        for (SimulatedHome home : registry.allHomes()) {
            for (SimulatedAppliance appliance : home.getAppliances()) {
                double watt = generateWatt(appliance);
                TelemetryMessage message = new TelemetryMessage(
                        home.getHomeId(), appliance.getApplianceId(), watt, Instant.now());
                publish(message);
            }
        }
    }

    private double generateWatt(SimulatedAppliance appliance) {
        boolean spike = ThreadLocalRandom.current().nextDouble() < ANOMALY_CHANCE;
        double base = appliance.getMaxSafeWatt() * ThreadLocalRandom.current().nextDouble(0.4, 0.95);
        double spikeAmount = spike ? appliance.getMaxSafeWatt() * ThreadLocalRandom.current().nextDouble(1.1, 1.4) : 0;
        return spike ? spikeAmount : base;
    }

    private void publish(TelemetryMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(telemetryTopic, message.homeId().toString(), json);
        } catch (Exception e) {
            log.warn("Failed to publish telemetry for home {} appliance {}",
                    message.homeId(), message.applianceId(), e);
        }
    }
}
