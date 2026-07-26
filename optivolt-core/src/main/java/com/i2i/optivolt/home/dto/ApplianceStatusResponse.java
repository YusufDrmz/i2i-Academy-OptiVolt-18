package com.i2i.optivolt.home.dto;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplianceStatusResponse {
    private Long id;
    private String name;
    private double currentWatt;
    private double maxSafeWatt;
    private boolean isAnomalous;
    private int consecutiveBreaches;
}
 