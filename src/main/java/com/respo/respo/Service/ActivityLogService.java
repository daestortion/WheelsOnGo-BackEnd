package com.respo.respo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.respo.respo.Entity.ActivityLogEntity;
import com.respo.respo.Repository.ActivityLogRepository;

import java.util.List;

@Service
public class ActivityLogService {
    
    @Autowired
    private ActivityLogRepository logRepository;

    public ActivityLogEntity logActivity(String action, String username) {
        ActivityLogEntity log = new ActivityLogEntity(action, username);
        
        // Print log to check if it's being created
        System.out.println("Logging Activity: " + action + " for " + username);
        
        return logRepository.save(log);
    }

    public List<ActivityLogEntity> getLogsByAction(String action) {
        return logRepository.findByActionContaining(action); // Query logs by action content
    }

    public List<ActivityLogEntity> getAllLogs() {
        return logRepository.findAll();
    }
}

