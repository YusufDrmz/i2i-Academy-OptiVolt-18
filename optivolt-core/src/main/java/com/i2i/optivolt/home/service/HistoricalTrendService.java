package com.i2i.optivolt.home.service;

import com.i2i.optivolt.home.entity.DailyConsumptionHistory;
import com.i2i.optivolt.home.repository.DailyConsumptionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricalTrendService {
    private final DailyConsumptionHistoryRepository historyRepository;

    public List<DailyConsumptionHistory> getHistoricalTrend(Long homeId) {
        return historyRepository.findByHomeIdOrderByDateDesc(homeId);
    }
}
