package com.i2i.optivolt.home.entity;
 
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
 
@Entity
@Table(name = "homes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Home {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    private String name;
 
    private String address;
 
    @Column(nullable = false)
    private String contactEmail;
 
    private Double maxPowerBudget;
    private Double maxFinancialBudget;
 
    private Double standardRate;
 
    private Double penaltyRate;
 
    @OneToMany(mappedBy = "home", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appliance> appliances;
 
    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt;
 
    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
    }
}