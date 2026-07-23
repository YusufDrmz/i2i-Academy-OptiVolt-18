package com.i2i.telemetrysensors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelemetrySensorsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelemetrySensorsApplication.class, args);
    }
}