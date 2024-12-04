package com.respo.respo.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.respo.respo.Entity.RequestFormEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Repository.RequestFormRepository;
import com.respo.respo.Service.UserService;
import com.respo.respo.Service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;  // Inject UserService to fetch use

    @Autowired
    private RequestFormRepository requestFormRepository;

    @GetMapping("/all")
    public List<WalletEntity> getAllWallets() {
        return walletService.getAllWallets();
    }

    // Existing method to get wallet by userId
    @GetMapping("/{id}")
    public WalletEntity getWalletById(@PathVariable int id) {
        return walletService.getWalletById(id);
    }

    @PostMapping
    public WalletEntity createWallet(@RequestBody WalletEntity walletEntity) {
        return walletService.createWallet(walletEntity);
    }

    @PutMapping("/addFunds")
    public ResponseEntity<?> addFunds(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        
        // Ensure amount is treated as Double
        Object amountObj = request.get("amount");
        Double amount = null;
        
        if (amountObj instanceof Double) {
            amount = (Double) amountObj;
        } else if (amountObj instanceof Integer) {
            amount = ((Integer) amountObj).doubleValue();
        }
        
        // Log received values
        System.out.println("Received userId: " + userId + ", amount: " + amount);
        
        if (userId == null || amount == null) {
            // Log missing parameters for debugging
            System.out.println("Error: userId or amount is null");
            return new ResponseEntity<>("userId or amount is null", HttpStatus.BAD_REQUEST);
        }
        
        WalletEntity updatedWallet = walletService.addFundsToWallet(userId, amount);
        if (updatedWallet != null) {
            return new ResponseEntity<>(updatedWallet, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/getRefundDetails/{userId}")
    public ResponseEntity<?> getRefundDetails(@PathVariable int userId) {
        // Fetch refund details from the service
        WalletService.RefundDetails refundDetails = walletService.getRefundDetails(userId);

        if (refundDetails == null) {
            // If wallet not found, return 404
            return new ResponseEntity<>("Wallet not found for userId: " + userId, HttpStatus.NOT_FOUND);
        }

        // Return refund details as a response
        return ResponseEntity.ok(refundDetails);
    }

}