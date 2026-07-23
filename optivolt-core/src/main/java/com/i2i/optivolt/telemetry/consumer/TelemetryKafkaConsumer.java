package com.i2i.optivolt.telemetry.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i2i.optivolt.config.KafkaConfig;
import com.i2i.optivolt.telemetry.dto.TelemetryMessage;
import com.i2i.optivolt.telemetry.service.TelemetryProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetryKafkaConsumer {

    private final TelemetryProcessingService telemetryProcessingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaConfig.TELEMETRY_STREAM_TOPIC, groupId = "optivolt-core-group")
    public void consumeTelemetry(String messageJson) {
        try {
            TelemetryMessage message = objectMapper.readValue(messageJson, TelemetryMessage.class);
            telemetryProcessingService.processTelemetry(message);
        } catch (Exception e) {
            log.error("Failed to process telemetry message: {}", messageJson, e);
        }
    }
}
