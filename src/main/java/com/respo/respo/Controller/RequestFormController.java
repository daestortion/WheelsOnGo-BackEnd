package com.respo.respo.Controller;

import com.respo.respo.Entity.RequestFormEntity;
import com.respo.respo.Service.RequestFormService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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

    // Get a specific request by its ID
    @GetMapping("/getRequestById/{requestId}")
    public RequestFormEntity getRequestById(@PathVariable int requestId) {
        return requestFormService.getRequestById(requestId);
    }

    @PutMapping("/update/{requestId}")
    public ResponseEntity<RequestFormEntity> updateRequest(
            @PathVariable int requestId,
            @RequestParam(value = "proofImage", required = false) MultipartFile proofImage) {
        try {
            RequestFormEntity newRequestFormDetails = new RequestFormEntity();

            // If a proof image is uploaded, convert it to a byte array
            if (proofImage != null && !proofImage.isEmpty()) {
                newRequestFormDetails.setProofImage(proofImage.getBytes());
            }

            // Update the request and return the updated entity
            RequestFormEntity updatedRequest = requestFormService.updateRequest(requestId, newRequestFormDetails);
            return new ResponseEntity<>(updatedRequest, HttpStatus.OK);

        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
