package com.i2i.optivolt.home.service;
 
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i2i.optivolt.config.KafkaConfig;
import com.i2i.optivolt.home.dto.HomeRegistrationRequest;
import com.i2i.optivolt.home.entity.Appliance;
import com.i2i.optivolt.home.entity.Home;
import com.i2i.optivolt.home.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
@Slf4j
public class HomeRegistrationService {
 
    private static final double DEFAULT_STANDARD_RATE = 2.5;
    private static final double DEFAULT_PENALTY_RATE = 5.0;
 
    private final HomeRepository homeRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
 
    @Transactional
    public Home registerHome(HomeRegistrationRequest request) {
        Home home = Home.builder()
                .name(request.getName())
                .address(request.getAddress())
                .contactEmail(request.getContactEmail())
                .maxPowerBudget(request.getMaxPowerBudget())
                .maxFinancialBudget(request.getMaxFinancialBudget())
                .standardRate(request.getStandardRate() != null ? request.getStandardRate() : DEFAULT_STANDARD_RATE)
                .penaltyRate(request.getPenaltyRate() != null ? request.getPenaltyRate() : DEFAULT_PENALTY_RATE)
                .build();
 
        if (request.getAppliances() != null) {
            home.setAppliances(request.getAppliances().stream().map(dto -> {
                Appliance appliance = Appliance.builder()
                        .name(dto.getName())
                        .type(dto.getType())
                        .safePowerLimit(dto.getSafePowerLimit())
                        .home(home)
                        .build();
                return appliance;
            }).collect(Collectors.toList()));
        }
 
        Home savedHome = homeRepository.save(home);
        log.info("Registered new home with ID: {}", savedHome.getId());
 
        try {
            String jsonPayload = objectMapper.writeValueAsString(savedHome);
            kafkaTemplate.send(KafkaConfig.ASSET_REGISTRATION_TOPIC, String.valueOf(savedHome.getId()), jsonPayload);
            log.info("Published home registration event to Kafka for home ID: {}", savedHome.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize home object for Kafka publishing", e);
        }
 
        return savedHome;
    }
}