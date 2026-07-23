package com.i2i.optivolt.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class IgniteConfig {

    @Value("${ignite.client-mode:true}")
    private boolean clientMode;

    @Value("${ignite.addresses:localhost:47500..47509}")
    private String[] addresses;

    public static final String HOME_METRICS_CACHE = "home-metrics-cache";
    public static final String DEVICE_ANOMALY_CACHE = "device-anomaly-cache";

    @Bean
    public Ignite igniteInstance() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(clientMode);
        cfg.setPeerClassLoadingEnabled(true);

        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList(addresses));
        spi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(spi);

        CacheConfiguration<String, Object> homeMetricsCache = new CacheConfiguration<>(HOME_METRICS_CACHE);
        CacheConfiguration<String, Integer> deviceAnomalyCache = new CacheConfiguration<>(DEVICE_ANOMALY_CACHE);

        cfg.setCacheConfiguration(homeMetricsCache, deviceAnomalyCache);

        return Ignition.start(cfg);
    }
}
