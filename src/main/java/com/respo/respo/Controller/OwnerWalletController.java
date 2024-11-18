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
import com.respo.respo.Service.OwnerWalletService;

@RestController
@RequestMapping("/ownerWallet")
@CrossOrigin(origins = "http://main--wheelsongo.netlify.app", allowedHeaders = "*", allowCredentials = "true")
public class OwnerWalletController {

    @Autowired
    private OwnerWalletService ownerWalletService;

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
}
