package com.i2i.optivolt.home.dto;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryPointResponse {
    private String date;
    private double totalKwh;
    private double totalCost;
}
