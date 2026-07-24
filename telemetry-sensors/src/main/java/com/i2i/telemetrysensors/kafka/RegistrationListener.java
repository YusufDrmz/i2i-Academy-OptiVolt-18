package com.i2i.telemetrysensors.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i2i.telemetrysensors.dto.RegistrationMessage;
import com.i2i.telemetrysensors.registry.SimulationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationListener {

    private final SimulationRegistry registry;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${voltwise.kafka.topic.registration}",
            groupId = "${spring.kafka.consumer.group-id:telemetry-sensors-group}"
    )
    public void onRegistration(String payload) {
        try {
            RegistrationMessage message = objectMapper.readValue(payload, RegistrationMessage.class);
            registry.addOrUpdate(message);
            log.info("Registered home {} with {} appliances into simulation (total homes now: {})",
                    message.homeId(), message.appliances().size(), registry.homeCount());
        } catch (Exception e) {
            log.warn("Could not parse registration message, ignoring: {}", payload, e);
        }
    }
}
