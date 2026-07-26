package com.i2i.optivolt.home.dto;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeSummaryResponse {
    private Long id;
    private String name;
    private String address;
    private String contactEmail;
    private double currentWatt;
    private double powerQuotaWatt;
    private double budgetQuotaTry;
    private double currentCostTry;
    private String status;
    private int appliancesCount;
}