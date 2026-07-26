package com.i2i.optivolt.home.dto;
 
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import java.util.List;
 
@Data
public class HomeRegistrationRequest {
 
    private String name;
    private String address;
    private String contactEmail;
 
    @JsonAlias("powerQuotaWatt")
    private Double maxPowerBudget;
 
    @JsonAlias("budgetQuotaTry")
    private Double maxFinancialBudget;
 
    private Double standardRate;
    private Double penaltyRate;
 
    private List<ApplianceDto> appliances;
}
 