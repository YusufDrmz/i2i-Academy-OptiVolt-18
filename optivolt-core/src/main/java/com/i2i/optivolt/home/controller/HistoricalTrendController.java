package com.i2i.optivolt.home.controller;

import com.i2i.optivolt.home.entity.DailyConsumptionHistory;
import com.i2i.optivolt.home.service.HistoricalTrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/homes")
@RequiredArgsConstructor
@Tag(name = "Historical Trend", description = "Endpoints for fetching historical consumption trends from PostgreSQL")
public class HistoricalTrendController {

    private final HistoricalTrendService historicalTrendService;

    @GetMapping("/{homeId}/trend")
    @Operation(summary = "Fetch daily consumption history for a home")
    public ResponseEntity<List<DailyConsumptionHistory>> getTrend(@PathVariable Long homeId) {
        return ResponseEntity.ok(historicalTrendService.getHistoricalTrend(homeId));
    }
}
