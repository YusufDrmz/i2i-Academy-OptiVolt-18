package com.i2i.telemetrysensors.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SimulatedHome {
    private Long homeId;
    private List<SimulatedAppliance> appliances;
}
