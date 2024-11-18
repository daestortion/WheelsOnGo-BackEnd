package com.respo.respo.Controller;

import com.respo.respo.Entity.RequestFormEntity;
import com.respo.respo.Service.RequestFormService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/request-form")
public class RequestFormController {

    @Autowired
    private RequestFormService requestFormService;

    // Get all requests
    @GetMapping("/getAllRequests")
    public List<RequestFormEntity> getAllRequests() {
        return requestFormService.getAllRequests();
    }

    // Create a new request
    @PostMapping("/request-funds")
    public RequestFormEntity createRequest(@RequestParam int userId, @RequestBody RequestFormEntity requestForm) {
        return requestFormService.createRequest(userId, requestForm);
    }

    @GetMapping("/getUserRequests/{userId}")
    public List<RequestFormEntity> getUserRequests(@PathVariable int userId) {
        return requestFormService.getRequestsByUserId(userId);
    }

    // Approve a specific request
    @PutMapping("/approveRequest/{requestId}")
    public RequestFormEntity approveRequest(@PathVariable int requestId) {
        return requestFormService.approveRequest(requestId);
    }

}
