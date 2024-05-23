package com.respo.respo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    @Autowired
    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearCache(String cacheName) {
        Objects.requireNonNull(cacheManager.getCache(cacheName), "Cache not found: " + cacheName).clear();
    }

    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> clearCache(cacheName));
    }
}
