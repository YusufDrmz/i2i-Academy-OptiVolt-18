package com.i2i.telemetrysensors.registry;

import com.i2i.telemetrysensors.dto.RegistrationMessage;
import com.i2i.telemetrysensors.model.SimulatedAppliance;
import com.i2i.telemetrysensors.model.SimulatedHome;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component

public class SimulationRegistry {
    private final Map<Long, SimulatedHome> homes = new ConcurrentHashMap<>();

    public void addOrUpdate(RegistrationMessage message) {
        List<SimulatedAppliance> appliances = message.appliances().stream()
                .map(a -> new SimulatedAppliance(a.applianceId(), a.deviceName(), a.deviceType(), a.maxSafeWatt()))
                .collect(Collectors.toList());

        homes.put(message.homeId(), new SimulatedHome(message.homeId(), appliances));
    }

    public Collection<SimulatedHome> allHomes() {
        return homes.values();
    }

    public int homeCount() {
        return homes.size();
    }
}
