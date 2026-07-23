package com.i2i.optivolt.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String ASSET_REGISTRATION_TOPIC = "asset-registration-topic";
    public static final String TELEMETRY_STREAM_TOPIC = "telemetry-stream-topic";

    @Bean
    public NewTopic assetRegistrationTopic() {
        return TopicBuilder.name(ASSET_REGISTRATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic telemetryStreamTopic() {
        return TopicBuilder.name(TELEMETRY_STREAM_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
