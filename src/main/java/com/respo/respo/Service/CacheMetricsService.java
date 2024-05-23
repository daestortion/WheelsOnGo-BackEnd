package com.respo.respo.Service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class CacheMetricsService {

    private final MeterRegistry registry;

    // Constructor injection for MeterRegistry
    public CacheMetricsService(MeterRegistry registry) {
        this.registry = registry;
        registerCustomMetrics();
    }

    private void registerCustomMetrics() {
        // Register the gauge with a lambda expression that calls the instance method
        registry.gauge("cache.memory.usage", this, CacheMetricsService::calculateMemoryUsage);
    }

    // This should be a method that calculates or retrieves the cache memory usage
    // Assuming a simple calculation for demonstration; replace this with actual cache querying logic
    public double calculateMemoryUsage() {
        // Dummy implementation, replace with actual logic
        return Math.random() * 100;  // Simulates cache memory usage
    }
}
