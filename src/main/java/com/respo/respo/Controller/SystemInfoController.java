package com.respo.respo.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
public class SystemInfoController {

    @GetMapping("/heap-info")
    public String getHeapInfo() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();  // Maximum amount of memory the JVM will attempt to use
        long totalMemory = runtime.totalMemory();  // Total memory currently available to the JVM
        long freeMemory = runtime.freeMemory();  // An approximation to the total amount of memory currently available for future allocated objects

        return String.format("Max Memory: %d bytes%nTotal Memory: %d bytes%nFree Memory: %d bytes",
                             maxMemory, totalMemory, freeMemory);
    }
}
