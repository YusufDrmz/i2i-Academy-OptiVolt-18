package com.i2i.optivolt.home.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "daily_consumption_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyConsumptionHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id", nullable = false)
    @JsonIgnore
    private Home home;

    private LocalDate date;
    private Double totalConsumption;
    private Double totalBilledAmount;
}
