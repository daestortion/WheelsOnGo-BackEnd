package com.respo.respo.Controller;

import com.respo.respo.Service.CacheMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    private final CacheMetricsService cacheMetricsService;

    @Autowired
    public MetricsController(CacheMetricsService cacheMetricsService) {
        this.cacheMetricsService = cacheMetricsService;
    }

    @GetMapping("/cacheUsage")
    public double getCacheMemoryUsage() {
        // Use the instance of CacheMetricsService to call the non-static method
        return cacheMetricsService.calculateMemoryUsage();
    }
}
