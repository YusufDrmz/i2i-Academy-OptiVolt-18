package com.i2i.optivolt.home.controller;
 
import com.i2i.optivolt.home.dto.HistoryPointResponse;
import com.i2i.optivolt.home.dto.HomeDetailResponse;
import com.i2i.optivolt.home.dto.HomeSummaryResponse;
import com.i2i.optivolt.home.entity.DailyConsumptionHistory;
import com.i2i.optivolt.home.service.HistoricalTrendService;
import com.i2i.optivolt.home.service.HomeSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
 
@RestController
@RequestMapping("/api/homes")
@RequiredArgsConstructor
@Tag(name = "Home Query", description = "Dashboard-facing endpoints: list/detail/history")
public class HomeQueryController {
 
    private final HomeSummaryService homeSummaryService;
    private final HistoricalTrendService historicalTrendService;
 
    @GetMapping
    @Operation(summary = "List all homes with live status for the dashboard")
    public ResponseEntity<List<HomeSummaryResponse>> getAllHomes() {
        return ResponseEntity.ok(homeSummaryService.getAllSummaries());
    }
 
    @GetMapping("/{homeId}")
    @Operation(summary = "Fetch one home's live detail (appliances + metrics) for the dashboard")
    public ResponseEntity<HomeDetailResponse> getHome(@PathVariable Long homeId) {
        try {
            return ResponseEntity.ok(homeSummaryService.getDetail(homeId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
 
    @GetMapping("/{homeId}/history")
    @Operation(summary = "Fetch 7-day consumption trend, shaped for the dashboard chart")
    public ResponseEntity<List<HistoryPointResponse>> getHistory(@PathVariable Long homeId) {
        List<DailyConsumptionHistory> history = historicalTrendService.getHistoricalTrend(homeId);
        List<HistoryPointResponse> points = history.stream()
                .map(h -> HistoryPointResponse.builder()
                        .date(h.getDate() != null ? h.getDate().toString() : "")
                        .totalKwh(h.getTotalConsumption() != null ? h.getTotalConsumption() : 0.0)
                        .totalCost(h.getTotalBilledAmount() != null ? h.getTotalBilledAmount() : 0.0)
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(points);
    }
}