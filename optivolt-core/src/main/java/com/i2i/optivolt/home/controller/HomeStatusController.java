package com.i2i.optivolt.home.controller;

import com.i2i.optivolt.home.dto.HomeMetrics;
import com.i2i.optivolt.home.service.HomeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/homes")
@RequiredArgsConstructor
@Tag(name = "Home Status", description = "Endpoints for fetching live home status from Ignite")
public class HomeStatusController {

    private final HomeStatusService homeStatusService;

    @GetMapping("/{homeId}/status")
    @Operation(summary = "Fetch live home metrics from Ignite cache")
    public ResponseEntity<HomeMetrics> getHomeStatus(@PathVariable Long homeId) {
        HomeMetrics metrics = homeStatusService.getHomeStatus(homeId);
        if (metrics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(metrics);
    }
}
