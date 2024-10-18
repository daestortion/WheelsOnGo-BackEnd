package com.respo.respo.Controller;

import com.respo.respo.Entity.ActivityLogEntity;
import com.respo.respo.Service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityLogController {

    @Autowired
    private ActivityLogService logService;

    @GetMapping("/logs")
    public List<ActivityLogEntity> getAllLogs() {
        return logService.getAllLogs();
    }
}
