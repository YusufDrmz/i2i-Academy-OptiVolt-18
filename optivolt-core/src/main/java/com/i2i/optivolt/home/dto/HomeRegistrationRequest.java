package com.i2i.optivolt.home.dto;

import lombok.Data;
import java.util.List;

@Data
public class HomeRegistrationRequest {
    private String contactEmail;
    private Double maxPowerBudget;
    private Double maxFinancialBudget;
    private List<ApplianceDto> appliances;
}
