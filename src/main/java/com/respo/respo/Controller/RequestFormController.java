package com.respo.respo.Controller;

import com.respo.respo.Entity.RequestFormEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Repository.RequestFormRepository;
import com.respo.respo.Service.RequestFormService;
import com.respo.respo.Service.WalletService;

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
    @Autowired
    private WalletService walletService;  
    @Autowired
    private RequestFormRepository requestFormRepository;

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

    @GetMapping("/getProofImage/{requestId}")
    public ResponseEntity<byte[]> getProofImage(@PathVariable int requestId) {
        RequestFormEntity request = requestFormService.getRequestById(requestId);

        if (request.getProofImage() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "image/png") // Or other image formats
                .body(request.getProofImage());
    }

    // New endpoint for renters to request their refundable amount
    @PostMapping("/request-refund/{userId}")
    public ResponseEntity<String> requestRefund(@PathVariable int userId, @RequestBody RequestFormEntity requestForm) {
        try {
            String message = requestFormService.requestRefund(userId, requestForm);
            return new ResponseEntity<>(message, HttpStatus.CREATED);  // Return success message
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);  // Return error message if the refund exceeds balance
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to create refund request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);  // Handle unexpected errors
        }
    }

    // Request validation before submitting refund
    @PostMapping("/validate-renter-refund/{userId}")
    public ResponseEntity<String> validateRenterRefund(@PathVariable int userId, @RequestBody RequestFormEntity requestForm) {
        try {
            String message = requestFormService.validateRenterRefund(userId, requestForm);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); 
        }
    }

    // Approve renter's refund request
    @PutMapping("/approve-renter-refund/{requestId}")
    public ResponseEntity<RequestFormEntity> approveRenterRefund(@PathVariable int requestId) {
        try {
            // Fetch the request by its ID
            RequestFormEntity request = requestFormService.getRequestById(requestId);
            
            // Ensure the request is for a renter (UserType should be Renter)
            if (!"Renter".equals(request.getUserType())) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  // Return bad request if not a renter
            }
            
            // Proceed with the refund process
            WalletEntity renterWallet = walletService.getWalletByUserId(request.getUser().getUserId());
            double availableBalance = renterWallet.getBalance();
            double requestedAmount = request.getAmount();
            
            // Check if the renter has sufficient balance
            if (requestedAmount <= availableBalance) {
                // Deduct the requested amount from the renter's wallet
                renterWallet.setBalance(availableBalance - requestedAmount);
                walletService.save(renterWallet);  // Save the updated wallet
                
                // Mark the request as approved
                request.setStatus("approved");
                requestFormRepository.save(request);  // Use the repository's save method
                
                return new ResponseEntity<>(request, HttpStatus.OK);  // Return approved request
            } else {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  // Insufficient balance
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  // Handle unexpected errors
        }
    }

    @PutMapping("/denyRequest/{requestId}")
public ResponseEntity<String> denyRequest(@PathVariable int requestId) {
    try {
        // Fetch the request by its ID
        RequestFormEntity request = requestFormService.getRequestById(requestId);

        // Set the status to "denied"
        request.setStatus("denied");

        // Save the updated request
        requestFormRepository.save(request);

        return new ResponseEntity<>("Request denied successfully!", HttpStatus.OK);
    } catch (NoSuchElementException e) {
        return new ResponseEntity<>("Request not found with ID: " + requestId, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
        return new ResponseEntity<>("Error processing denial: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


}
