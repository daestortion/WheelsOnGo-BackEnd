package com.respo.respo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.respo.respo.Entity.OwnerWalletEntity;
import com.respo.respo.Entity.RequestFormEntity;
import com.respo.respo.Service.OwnerWalletService;
import com.respo.respo.Service.RequestFormService;
import com.respo.respo.Repository.RequestFormRepository;

@RestController
@RequestMapping("/ownerWallet")
@CrossOrigin(origins = "http://main--wheelsongo.netlify.app", allowedHeaders = "*", allowCredentials = "true")
public class OwnerWalletController {

    @Autowired
    private OwnerWalletService ownerWalletService;

    @Autowired
    private RequestFormService requestFormService;

    @Autowired
    private RequestFormRepository requestFormRepository;

    @GetMapping("/getWalletByUserId/{userId}")
    public ResponseEntity<OwnerWalletEntity> getWalletByUserId(@PathVariable int userId) {
        OwnerWalletEntity wallet = ownerWalletService.getWalletByUserId(userId);
        return wallet != null ? ResponseEntity.ok(wallet) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/createOrUpdateWallet")
    public ResponseEntity<OwnerWalletEntity> createOrUpdateWallet(@RequestBody OwnerWalletEntity wallet) {
        OwnerWalletEntity updatedWallet = ownerWalletService.createOrUpdateWallet(wallet);
        return ResponseEntity.ok(updatedWallet);
    }

    @PutMapping("/addToOnlineEarnings/{userId}")
    public ResponseEntity<String> addToOnlineEarnings(@PathVariable int userId, @RequestParam float amount) {
        ownerWalletService.addToOnlineEarnings(userId, amount);
        return ResponseEntity.ok("85% of the amount added to online earnings successfully.");
    }

    @PutMapping("/addToCashEarnings/{userId}")
    public ResponseEntity<String> addToCashEarnings(@PathVariable int userId, @RequestParam float amount) {
        ownerWalletService.addToCashEarnings(userId, amount);
        return ResponseEntity.ok("15% of the amount added to cash earnings successfully.");
    }

    @PutMapping("/updateCashRefundable/{userId}")
    public ResponseEntity<String> updateCashRefundable(@PathVariable int userId, @RequestParam float amount) {
        ownerWalletService.updateCashRefundable(userId, amount);
        return ResponseEntity.ok("Cash refundable updated successfully.");
    }

    // Fetch wallet details for a specific user
    @GetMapping("/getWalletDetails/{userId}")
    public ResponseEntity<OwnerWalletEntity> getWalletDetails(@PathVariable int userId) {
        OwnerWalletEntity wallet = ownerWalletService.getWalletByUserId(userId);
        if (wallet != null) {
            return ResponseEntity.ok(wallet);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/deductRefund/{userId}")
    public ResponseEntity<String> deductRefund(@PathVariable int userId, @RequestParam double refundAmount) {
        boolean success = ownerWalletService.deductRefundAmount(userId, refundAmount);
        
        if (success) {
            return ResponseEntity.ok("Refund of ₱" + refundAmount + " deducted from owner's wallet successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to deduct refund from owner's wallet.");
        }
    }

    @PutMapping("/deductFromOnlineEarnings/{userId}/{requestId}")
    public ResponseEntity<String> deductFromOnlineEarnings(@PathVariable int userId, @PathVariable int requestId) {
        // Fetch the request details
        RequestFormEntity request = requestFormService.getRequestById(requestId);
        if (!request.getStatus().equals("approved")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Request is not approved yet.");
        }

        // Validate that the user has sufficient funds
        OwnerWalletEntity wallet = ownerWalletService.getWalletByUserId(userId);
        if (wallet.getOnlineEarning() < request.getAmount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Insufficient online earnings.");
        }

        // Deduct the amount from the user's online earnings
        boolean success = ownerWalletService.deductRefundAmount(userId, request.getAmount());
        if (!success) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to deduct funds from wallet.");
        }

        // Mark the request as completed
        request.setStatus("completed");
        requestFormRepository.save(request);
        
        return ResponseEntity.ok("Amount successfully deducted from online earnings.");
    }

}
