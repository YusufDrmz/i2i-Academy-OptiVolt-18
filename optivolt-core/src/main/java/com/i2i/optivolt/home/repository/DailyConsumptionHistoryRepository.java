package com.i2i.optivolt.home.repository;

import com.i2i.optivolt.home.entity.DailyConsumptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DailyConsumptionHistoryRepository extends JpaRepository<DailyConsumptionHistory, Long> {
    List<DailyConsumptionHistory> findByHomeIdOrderByDateDesc(Long homeId);
}
