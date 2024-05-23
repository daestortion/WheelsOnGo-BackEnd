package com.respo.respo.Controller;

import com.respo.respo.Service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService cacheService;

    @Autowired
    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/clear/{cacheName}")
    public ResponseEntity<String> clearCache(@PathVariable String cacheName) {
        try {
            cacheService.clearCache(cacheName);
            return ResponseEntity.ok("Cache cleared successfully: " + cacheName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/clearAll")
    public ResponseEntity<String> clearAllCaches() {
        cacheService.clearAllCaches();
        return ResponseEntity.ok("All caches cleared successfully");
    }
}
