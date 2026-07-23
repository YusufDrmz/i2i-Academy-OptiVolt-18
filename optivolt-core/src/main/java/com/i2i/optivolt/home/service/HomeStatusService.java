package com.i2i.optivolt.home.service;

import com.i2i.optivolt.config.IgniteConfig;
import com.i2i.optivolt.home.dto.HomeMetrics;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class HomeStatusService {

    private final Ignite ignite;
    private IgniteCache<String, HomeMetrics> homeMetricsCache;

    @PostConstruct
    public void init() {
        this.homeMetricsCache = ignite.cache(IgniteConfig.HOME_METRICS_CACHE);
    }

    public HomeMetrics getHomeStatus(Long homeId) {
        return homeMetricsCache.get(String.valueOf(homeId));
    }
}
